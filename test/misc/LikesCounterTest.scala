package misc

import models.LikesCounter
import org.scalatest.FunSuite

class LikesCounterTest extends FunSuite {
  test("Likes Counter"){
    LikesCounter.like("a")
    LikesCounter.like("b")
    (1 to 5).foreach(_=>LikesCounter.like("c"))
    (1 to 345).foreach(_=>LikesCounter.like("b"))
    assert(LikesCounter.countLikes(Seq("a","b","c")) == Seq(1,346,5))
  }
}
