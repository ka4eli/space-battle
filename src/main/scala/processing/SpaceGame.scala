package processing

import akka.actor._
import board.{HexEnemyBoard, HexPlayerBoard}
import grid.Grid.ShotResult.ShotResult
import grid.HexGrid
import models.{NewGameRequest, User}
import processing.Receptionist.{Fire, Salvo}

import scala.util.{Failure, Success, Try}

class SpaceGame(player: User, gameRequest: NewGameRequest, gameId: String, isFirst: Boolean, rules: Option[String] = None) extends Actor with ShipsGenerator {

  import SpaceGame._

  val playerBoard = new HexPlayerBoard(new HexGrid(generateShips))
  val enemyBoard = new HexEnemyBoard
  var auto = false

  lazy val autopilot = new Autopilot

  def receive = playing(isFirst)

  def playing(isMyTurn: Boolean): Receive = {

    case s@Salvo(gid, salvo) =>
      if (!isMyTurn) {
        Try(playerBoard.processSalvo(salvo)) match {
          case Success(res) =>
            if (playerBoard.shipsAlive < 1)
              sender ! SalvoResponse(res, Game(None, Some(gameRequest.userId)))
            else {
              sender ! SalvoResponse(res, Game(Some(gameRequest.userId)))
              if (auto) autopilot.shoot
            }
            context.become(playing(true))

          case Failure(e) => sender ! SalvoError(s, e)
        }
      } else sender ! WrongTurn


    case s@SalvoResponse(salvo, game) =>
      enemyBoard.processShotResults(salvo)
      context.become(playing(false))


    case f@Fire(_, _) =>
      if (isMyTurn) {
        val shooter = context.actorOf(Props(classOf[Shooter], gameRequest))
        shooter forward f
      } else sender ! WrongTurn

  }


}

object SpaceGame {

  def props(player: User, gameRequest: NewGameRequest, gameId: String, isFirst: Boolean, rules: Option[String]) =
    Props(classOf[SpaceGame], player, gameRequest, gameId)

  case class SalvoError(salvo: Salvo, t: Throwable)

  case class SalvoResponse(salvo: List[(String, ShotResult)], game: Game)

  case class Game(playerTurn: Option[String], won: Option[String] = None)

  case object WrongTurn

}
