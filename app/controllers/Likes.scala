package controllers


import java.util.concurrent.Executors

import javax.inject._
import models.LikesCounter
import play.api.mvc._

import scala.concurrent.ExecutionContext


@Singleton class Likes @Inject()(counter: LikesCounter, controllerComponents: ControllerComponents) extends AbstractController(controllerComponents){
  val persistenceExecutionContext:ExecutionContext = ExecutionContext.fromExecutorService(Executors.newSingleThreadExecutor())

  def like(title:String) = Action{
    val n = counter.like(title)(persistenceExecutionContext)
    Results.Ok(n.toString)
  }

  def countLikes(titles:String) = Action{
    val res = counter.countLikes(titles.split("/"))(persistenceExecutionContext)
    Results.Ok(res.mkString(","))
  }
}
