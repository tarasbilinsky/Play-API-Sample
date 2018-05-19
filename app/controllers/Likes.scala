package controllers


import models.LikesCounter
import play.api.mvc._
import javax.inject._

import scala.concurrent.ExecutionContext


@Singleton class Likes @Inject()(counter: LikesCounter, controllerComponents: ControllerComponents) extends AbstractController(controllerComponents){
  val crossOriginHeaders:(String,String) = ("'Access-Control-Allow-Origin","*")
  val persistenceExecutionContext:ExecutionContext = controllerComponents.executionContext //TODO separate execution context, db writes may be slower

  def like(title:String) = Action{
    val n = counter.like(title)(persistenceExecutionContext)
    Results.Ok(n.toString).withHeaders(crossOriginHeaders)
  }

  def countLikes(titles:String) = Action{
    val res = counter.countLikes(titles.split("/"))(persistenceExecutionContext)
    Results.Ok(res.mkString(",")).withHeaders(crossOriginHeaders)
  }
}
