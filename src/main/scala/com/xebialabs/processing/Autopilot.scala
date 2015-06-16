package com.xebialabs.processing

import akka.actor.{Actor, Props}
import com.xebialabs.board.{HexConversions, HexEnemyBoard}
import com.xebialabs.processing.HexAutopilot.{Resend, Shoot}
import com.xebialabs.processing.Receptionist.Fire
import com.xebialabs.processing.SpaceGame.SalvoResponse

import scala.concurrent.duration._
import scala.util.Random

trait Autopilot[T] {
  def salvo(n: Int): List[T]
}

class HexAutopilot(enemyBoard: HexEnemyBoard, gameId: String) extends Actor with Autopilot[String] with HexConversions {
  val r = Random
  var last = 0

  def receive = {
    case Shoot(n) =>
      last = n
      context.parent ! Fire(gameId, salvo(n))
    case Resend => context.parent ! Fire(gameId, salvo(last))
    case _: SalvoResponse =>
    case _ =>
      import context.dispatcher
      context.system.scheduler.scheduleOnce(0 millisecond, self, Resend)
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

  case object Resend

}