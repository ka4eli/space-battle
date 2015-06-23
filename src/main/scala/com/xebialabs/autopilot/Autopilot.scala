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
    def takeNoMoreThanNShoots(l: List[Ship], n: Int, acc: Set[Shot] = Set()): Set[Shot] = {
      if (l.isEmpty || n < 0) acc
      else {
        val newAcc = l.head ++ acc
        val newN = n - (newAcc.size - acc.size)
        takeNoMoreThanNShoots(l.tail, newN, newAcc.take(n))
      }
    }

    def loop(n: Int, hitList: List[Shot], acc: List[Shot] = Nil): List[Shot] = {
      if (n == 0 || hitList.isEmpty) acc
      else {
        val unknownFields = (for {
          i <- 0 to enemyBoard.size._1 - 1
          j <- 0 to enemyBoard.size._1 - 1
          if !enemyBoard.missed.contains(i, j)
        } yield (i, j)).toList

        val allRotations = unknownFields.flatMap(f => types.map(_(f))).flatMap(rotations).filter(_.contains(hitList.head)).filterNot(_.forall(enemyBoard.hit.contains))
        val safe = allRotations.filter(ship => isSafe(ship, enemyBoard.size, enemyBoard.missed))
        val mostProbable = safe.map(s => (s, s.count(enemyBoard.hit.contains))).sortBy(_._2).reverse.map(_._1)

        val xTaken = takeNoMoreThanNShoots(mostProbable.map(_.diff(enemyBoard.hit)), n)

        val shoots = xTaken.toList.filterNot(enemyBoard.hit.contains)
        if (shoots.size >= n) shoots.take(n) ::: acc
        else loop(n - shoots.size, hitList.tail, shoots ::: acc)
      }
    }

    val smart = loop(n, enemyBoard.hit.toList)
    println(s"Smart size is: ${smart.size}")
    if (smart.size < n) smart.map(shotToHex) ::: randomSalvo(n - smart.size, smart)
    else smart.map(shotToHex)
  }

}