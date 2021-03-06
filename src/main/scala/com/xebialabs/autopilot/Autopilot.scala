package com.xebialabs.autopilot

import com.xebialabs.board.{HexConversions, HexEnemyBoard}
import com.xebialabs.grid.Grid._
import com.xebialabs.ship._

import scala.util.Random

trait Autopilot[T] {
  def salvo(n: Int): List[T]
}

trait RandomSalvo extends HexConversions {
  val r = Random

  def enemyBoard: HexEnemyBoard

  def randomSalvo(n: Int, taken: List[Shot] = Nil): List[String] = {
    val unknown = for {
      i <- 0 to enemyBoard.size._1 - 1
      j <- 0 to enemyBoard.size._2 - 1
      if !enemyBoard.hit.contains(i, j) && !enemyBoard.missed.contains(i, j) && !taken.contains(i, j)
    } yield shotToHex(i, j)
    r.shuffle(unknown.toList).take(n)
  }
}

trait SimpleHexAutopilot extends Autopilot[String] with RandomSalvo {
  def salvo(n: Int) = randomSalvo(n)
}


trait SmartHexAutopilot extends Autopilot[String] with RandomSalvo with ShipsUtils {

  def salvo(n: Int): List[String] = {
    def takeNoMoreThanNShoots(l: List[Ship], i: Int, acc: List[Shot] = Nil): List[Shot] = {
      if (l.isEmpty || i < 0) acc
      else {
        val newAcc = (acc ::: l.head.toList).distinct
        val newN = i - (newAcc.size - acc.size)
        takeNoMoreThanNShoots(l.tail, newN, newAcc.take(n))
      }
    }

    def smartSalvo(n: Int): List[Shot] = {
      val unknownFields = (for {
        i <- 0 to enemyBoard.size._1 - 1
        j <- 0 to enemyBoard.size._1 - 1
        if !enemyBoard.missed.contains(i, j)
      } yield (i, j)).view

      val hitRotations = unknownFields.flatMap(f => types.map(_(f))).flatMap(rotations).filterNot(_.intersect(enemyBoard.hit).isEmpty).filterNot(_.forall(enemyBoard.hit.contains))
      val safe = hitRotations.filter(isSafe(_, enemyBoard.size, enemyBoard.missed))
      val ordered = safe.sortBy(-1 * _.count(enemyBoard.hit.contains))
      val diffedWithHit = ordered.map(_.diff(enemyBoard.hit))

      takeNoMoreThanNShoots(diffedWithHit.toList, n)
    }

    val smart = smartSalvo(n)
    if (smart.size < n) smart.map(shotToHex) ::: randomSalvo(n - smart.size, smart)
    else smart.map(shotToHex)
  }

}