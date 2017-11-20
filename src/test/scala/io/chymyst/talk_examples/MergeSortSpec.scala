package io.chymyst.talk_examples

import io.chymyst.jc._
import org.scalatest.{FlatSpec, Matchers}

class MergeSortSpec extends FlatSpec with Matchers {

  def arrayMerge(arr1: Array[Int], arr2: Array[Int]): Array[Int] = {
    arr1.headOption match {
      case Some(head1) ⇒ arr2.headOption match {
        case Some(head2) ⇒
          if (head1 < head2) Array(head1) ++ arrayMerge(arr1.tail, arr2) else Array(head2) ++ arrayMerge(arr1, arr2.tail)
        case None ⇒ arr1
      }
      case None ⇒ arr2
    }
  }

  def performMergeSort(array: Array[Int]): Array[Int] = {
    val mergesort = m[(Array[Int], M[Array[Int]])]

    val finished = b[Unit, Array[Int]]
    val finalResult = m[Array[Int]]

    site(
      go { case finalResult(arr) + finished(_, reply) ⇒ reply(arr) }
    )

    site(
      go { case mergesort((arr, sortedResult)) ⇒
        if (arr.length <= 1) sortedResult(arr)
        else {
          val sorted1 = m[Array[Int]]
          val sorted2 = m[Array[Int]]
          site(
            go { case sorted1(x) + sorted2(y) ⇒ sortedResult(arrayMerge(x, y)) }
          )
          val (part1, part2) = arr.splitAt(arr.length / 2)
          // Emit lower-level mergesort molecules:
          mergesort(part1, sorted1) + mergesort(part2, sorted2)
        }
      }
    )

    mergesort(array, finalResult)

    finished()
  }

  behavior of "merge sort"

  it should "merge arrays correctly" in {
    arrayMerge(Array(1, 2, 5), Array(3, 6)).toSeq shouldEqual Seq(1, 2, 3, 5, 6)
  }

  it should "fail to run livelock" in {
    val cc = m[Int]
    val r = go { case cc(x) => cc(x+1) }
  }

  it should "sort an array using concurrent merge-sort correctly" in {

    val count = 10

    val arr = Array.fill[Int](count)(scala.util.Random.nextInt(count))
    val expectedResult = arr.sorted

    performMergeSort(arr) shouldEqual expectedResult
  }
}
