package models

import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicLong

import com.google.inject.ImplementedBy
import io.getquill._
import javax.inject._

import scala.collection.concurrent.TrieMap
import scala.concurrent.{ExecutionContext, Future}


case class ResourceId(id:Long) extends AnyVal

case class Resource(id: ResourceId, title: String)

case class Like(resourceId: ResourceId, time: LocalDateTime = LocalDateTime.now())

object Resource{
  private lazy val ctx = new PostgresAsyncContext(SnakeCase, "db")
  import ctx._
  private val likeSchema = quote {querySchema[models.Like]("likes")}

  private val data = TrieMap[String, ResourceId]()

  def add(id:ResourceId, title: String):Unit = data.putIfAbsent(title,id)

  private val dummyResourceId = ResourceId(0L)
  def find(title: String)(implicit ec: ExecutionContext):Future[ResourceId] = {
    data.get(title).fold{
      val q = quote{
        query[Resource].insert(lift(Resource(dummyResourceId,title))).onConflictUpdate(_.title)( (t,e)=>t.title->e.title).returning(_.id)
      }
      ctx.run(q).map{id=>
        data.put(title,id)
        id
      }
    }(Future.successful)
  }

  def recordLike(id: ResourceId)(implicit ec: ExecutionContext):Future[Unit] = {
    val q = quote{
      likeSchema.insert(lift(models.Like(id)))
    }
    ctx.run(q).map(_=>())
  }

  def loadLikes(implicit ec: ExecutionContext):Future[Seq[(String,ResourceId,Long)]] = {
    val q = quote {
      likeSchema.join(query[Resource]).on( (l,r) => l.resourceId==r.id).groupBy{ case (_,r) => (r.title,r.id)}.map {
        case ( (title, id), rr) =>
          (title, id, rr.size)
      }
    }

    ctx.run(q)
  }
}

@ImplementedBy(classOf[LikesCounterPersistent])
trait LikesCounter {

  protected val data:TrieMap[String,AtomicLong] = new TrieMap
  def like(title: String)(implicit ec: ExecutionContext):Long = {
    val e = data.putIfAbsent(title, new AtomicLong(1L))
    e.map(_.incrementAndGet()).getOrElse(1L)
  }
  def countLikes(titles:Seq[String])(implicit ec: ExecutionContext):Seq[Long] = {
    titles.map{ t =>
      data.get(t).map(_.get()).getOrElse(0L)
    }
  }
}

@Singleton
class LikesCounterPersistent @Inject() (implicit ec: ExecutionContext) extends LikesCounter {

  Resource.loadLikes(ec).foreach { f =>
    f.foreach {
      case (title, id, n) =>
        Resource.add(id,title)
        val e = data.putIfAbsent(title, new AtomicLong(n))
        e.map(_.addAndGet(n))
    }
  }

  override def like(title: String)(implicit ec: ExecutionContext): Long = {
    Resource.find(title).map(Resource.recordLike)
    super.like(title)
  }
}
