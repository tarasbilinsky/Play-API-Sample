package models

import java.util.concurrent.atomic.AtomicLong

import scala.collection.concurrent
import scala.collection.concurrent.TrieMap
import scala.collection.parallel.mutable.ParTrieMap

class LikesCounter {

}

object LikesCounter {
  private val data:TrieMap[String,AtomicLong] = new TrieMap
  def like(title: String):Long = {
    val e = data.putIfAbsent(title, new AtomicLong(1L))
    e.map(_.incrementAndGet()).getOrElse(1L)
  }
  def countLikes(titles:Seq[String]):Seq[Long] = {
    titles.map{ t =>
      data.get(t).map(_.get()).getOrElse(0L)
    }
  }
}
