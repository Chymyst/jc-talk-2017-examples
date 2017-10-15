package io.chymyst.talk_examples

import io.chymyst.jc._

object DiningPhilosophers extends App {

  def randomWait(message: String): Unit = {
    Thread.sleep(math.floor(scala.util.Random.nextDouble * 20.0 + 2.0).toLong)
  }

  def eat(philosopher: Int): Unit = {
    randomWait(s"Philosopher $philosopher is eating")
  }

  def think(philosopher: Int): Unit = {
    randomWait(s"Philosopher $philosopher is thinking")
  }

  val t1 = m[Unit]
  val t2 = m[Unit]
  val t3 = m[Unit]
  val t4 = m[Unit]
  val t5 = m[Unit]
  val h1 = m[Unit]
  val h2 = m[Unit]
  val h3 = m[Unit]
  val h4 = m[Unit]
  val h5 = m[Unit]
  val f12 = m[Unit]
  val f23 = m[Unit]
  val f34 = m[Unit]
  val f45 = m[Unit]
  val f51 = m[Unit]

  site(
    go { case t1(_) ⇒ wait(); h1() },
    go { case t2(_) ⇒ wait(); h2() },
    go { case t3(_) ⇒ wait(); h3() },
    go { case t4(_) ⇒ wait(); h4() },
    go { case t5(_) ⇒ wait(); h5() },

    go { case h1(_) + f12(_) + f51(_) ⇒ wait(); t1() + f12() + f51() },
    go { case h2(_) + f23(_) + f12(_) ⇒ wait(); t2() + f23() + f12() },
    go { case h3(_) + f34(_) + f23(_) ⇒ wait(); t3() + f34() + f23() },
    go { case h4(_) + f45(_) + f34(_) ⇒ wait(); t4() + f45() + f34() },
    go { case h5(_) + f51(_) + f45(_) ⇒ wait(); t5() + f51() + f45() }
  )
  t1() + t2() + t3() + t4() + t5()
  f12() + f23() + f34() + f45() + f51()

}
