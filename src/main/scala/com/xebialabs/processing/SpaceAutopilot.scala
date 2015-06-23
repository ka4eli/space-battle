package com.xebialabs.processing

import akka.actor.{Actor, Props}
import com.xebialabs.autopilot.SmartHexAutopilot
import com.xebialabs.board.HexEnemyBoard
import com.xebialabs.processing.Receptionist.Fire
import com.xebialabs.processing.SpaceGame.SalvoResponse
import org.slf4j.LoggerFactory

import scala.concurrent.duration._

class SpaceAutopilot(val enemyBoard: HexEnemyBoard, gameId: String) extends Actor with SmartHexAutopilot {

  private lazy val log = LoggerFactory.getLogger(classOf[SpaceAutopilot])

  import SpaceAutopilot._

  var last = 0

  def receive = {
    case PrepareAndFire(n) =>
      last = n
      context.parent ! Fire(gameId, salvo(n))

    case Resend(m) =>
      log.info("Resending Fire. Resend message: {}", m)
      context.parent ! Fire(gameId, salvo(last))

    case _: SalvoResponse =>

    case m =>
      import context.dispatcher
      context.system.scheduler.scheduleOnce(0 millisecond, self, Resend(m))
  }

}

object SpaceAutopilot {

  def props(enemyBoard: HexEnemyBoard, gameId: String) = Props(classOf[SpaceAutopilot], enemyBoard, gameId)

  case class PrepareAndFire(cannons: Int)

  case class Resend(mes: Any)

}