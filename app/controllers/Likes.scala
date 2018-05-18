package controllers


import models.LikesCounter
import play.api.mvc.{InjectedController, Results}


class Likes extends InjectedController{
  val crossOriginHeaders = ("'Access-Control-Allow-Origin","*")
  def like(title:String) = Action{
    val n = LikesCounter.like(title)
    Results.Ok(n.toString).withHeaders(crossOriginHeaders)
  }

  def countLikes(titles:String) = Action{
    val res = LikesCounter.countLikes(titles.split("/"))
    Results.Ok(res.mkString(",")).withHeaders(crossOriginHeaders)
  }
}
