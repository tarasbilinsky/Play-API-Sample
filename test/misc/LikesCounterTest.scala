package misc

import models.LikesCounter
import org.scalatest.FunSuite

import scala.concurrent.ExecutionContext

class LikesCounterTest extends FunSuite {
  test("Likes Counter"){
    implicit val ec:ExecutionContext = ExecutionContext.global
    val likesCounter = new LikesCounter {}
    likesCounter.like("a")
    likesCounter.like("b")
    (1 to 5).foreach(_=>likesCounter.like("c"))
    (1 to 345).foreach(_=>likesCounter.like("b"))
    assert(likesCounter.countLikes(Seq("a","b","c")) == Seq(1,346,5))
  }
}
