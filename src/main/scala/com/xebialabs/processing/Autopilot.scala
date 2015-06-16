package com.xebialabs.processing

import akka.actor.{Actor, Props}
import com.xebialabs.board.{HexConversions, HexEnemyBoard}
import com.xebialabs.processing.HexAutopilot.Shoot
import com.xebialabs.processing.Receptionist.Fire

import scala.util.Random

trait Autopilot[T] {
  def salvo(n: Int): List[T]
}

class HexAutopilot(enemyBoard: HexEnemyBoard, gameId: String) extends Actor with Autopilot[String] with HexConversions {
  val r = Random

  def receive = {
    case Shoot(n) => context.parent ! Fire(gameId, salvo(n))
    case x => context.parent ! x
  }

  def salvo(n: Int): List[String] = {
    val unknown = for {
      i <- 0 to enemyBoard.size._1 - 1
      j <- 0 to enemyBoard.size._2 - 1
      if enemyBoard.board(i)(j) == "."
    } yield shotToHex(i, j)
    r.shuffle(unknown.toList).take(n)
  }
}

object HexAutopilot {

  def props(enemyBoard: HexEnemyBoard, gameId: String) = Props(classOf[HexAutopilot], enemyBoard, gameId)

  case class Shoot(cannons: Int)

}