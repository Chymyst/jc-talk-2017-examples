package io.chymyst.talk_examples

import io.chymyst.jc._
import org.scalatest.{FlatSpec, Matchers}

class FutureSpec extends FlatSpec with Matchers {

  behavior of "future"

  def futureOf[T](f: ⇒ T): B[Unit, T] = {
    val get = b[Unit, T]

    site(
      go { case get(_, reply) ⇒ reply(f) }
    )

    get
  }

  it should "produce a value" in {

    val f = futureOf {
      Thread.sleep(100)
      123
    }

    val result = f()

    result shouldEqual 123

  }

  behavior of "map/reduce"

  it should "perform a map/reduce-like computation" in {
    val count = 10000

    val initTime = System.currentTimeMillis()

    val res = m[List[Int]]
    val r = m[Int]
    val d = m[Int]
    val get = b[Unit, List[Int]]

    site(
      go { case d(n) => r(n * 2) },
      go { case res(list) + r(s) => res(s :: list) },
      go { case get(_, reply) + res(list) if list.size == count => reply(list) } // ignore warning: "non-variable type argument Int"
    )

    (1 to count).foreach(d(_))
    val expectedResult = (1 to count).map(_ * 2)
    res(Nil)

    get().toSet shouldEqual expectedResult.toSet

    println(s"map/reduce test with n=$count took ${System.currentTimeMillis() - initTime} ms")
  }


}
