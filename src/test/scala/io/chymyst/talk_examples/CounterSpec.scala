package io.chymyst.talk_examples

import org.scalatest.{FlatSpec, Matchers}

import io.chymyst.jc._

class CounterSpec extends FlatSpec with Matchers {

  behavior of "counter with blocking access"

  it should "unblock when counter reaches 0" in {

    val c = m[Int]
    val decr = m[Unit]

    val finished = m[Unit]
    val get = b[Unit, String]

    site(
      go { case finished(_) + get(_, reply) ⇒ reply("done") },
      go { case c(x) + decr(_) ⇒ if (x > 0) c(x - 1) },
      go { case c(0) ⇒ finished() }
    )

    val initTime = System.currentTimeMillis()

    val n = 10000

    c(n) // Emit initial value for the counter.

    (1 to n).foreach(_ ⇒ decr()) // Decrement `n` times.

    get() shouldEqual "done"

    val elapsed = System.currentTimeMillis() - initTime

    println(s"Elapsed $elapsed ms")
  }

}
