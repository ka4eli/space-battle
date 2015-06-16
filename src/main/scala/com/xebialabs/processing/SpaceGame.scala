package com.xebialabs.processing

import akka.actor._
import com.xebialabs.board.{HexEnemyBoard, HexPlayerBoard}
import com.xebialabs.grid.Grid.{Ship, ShotResult}
import com.xebialabs.grid.Grid.ShotResult.ShotResult
import com.xebialabs.grid.Grid.ShotResult._
import com.xebialabs.grid.HexGrid
import com.xebialabs.models.{SpaceshipProtocol, User}
import com.xebialabs.processing.HexAutopilot.Shoot
import com.xebialabs.processing.Receptionist.{Status, _}
import com.xebialabs.processing.Shooter.Salvo
import com.xebialabs.ship.DefaultShipsGenerator
import org.slf4j.LoggerFactory

import scala.util.{Failure, Success, Try}

class SpaceGame(user: User,
                opponent: User,
                gameId: String,
                remote: SpaceshipProtocol,
                isFirst: Boolean,
                rule: String,
                webClient: WebClient) extends Actor {

  private lazy val log = LoggerFactory.getLogger(classOf[SpaceGame])

  import SpaceGame._

  val generatedShips = user.shipsGenerator.map(_.generateShips).getOrElse(new DefaultShipsGenerator((16, 16)).generateShips)
  val playerBoard = new HexPlayerBoard(new HexGrid(generatedShips))
  val enemyBoard = new HexEnemyBoard
  var auto = false

  lazy val autopilot = context.actorOf(HexAutopilot.props(enemyBoard, gameId))
  lazy val shooter = context.actorOf(Shooter.props(remote, gameId, webClient))

  def receive = playing(isFirst)

  def playing(isMyTurn: Boolean): Receive = {

    //Protocol: i'm attacked
    case s@SalvoReq(gid, salvo) =>
      if (!isMyTurn) {
        Try(playerBoard.processSalvo(salvo)) match {
          case Success(res) =>
            if (playerBoard.shipsAlive < 1) {
              val gameState = Game(None, Some(opponent.userId))
              context.parent !(gid, GameStatus(player, enemy, gameState))
              sender ! SalvoResponse(reduceDuplicates(res), gameState)
            } else {
              val next =
                if (rule == "super-charge" && res.map(_._2).contains(ShotResult.Kill)) opponent.userId
                else user.userId
              sender ! SalvoResponse(reduceDuplicates(res), Game(Some(next)))
              context.become(playing(true))
              if (auto) autopilot ! Shoot(cannons)
            }

          case Failure(e) => sender ! SalvoError(s, e)
        }
      } else sender ! WrongTurn


    //Shooter: response from my fire
    case s@SalvoResponse(salvo, gameState) =>
      enemyBoard.processShotResults(salvo.toList)
      if (gameState.won.isEmpty) {
        val isMyTurn = gameState.playerTurn.get == user.userId
        context.become(playing(isMyTurn))
        if (isMyTurn && auto) autopilot ! Shoot(cannons)
      }
      else context.parent !(gameId, GameStatus(player, enemy, gameState))

    //User: trying to fire
    case f@Fire(gid, salvo) =>
      if (!isMyTurn) sender ! WrongTurn
      else if (salvo.size > cannons) sender ! NotEnoughCannons(cannons)
      else shooter forward Salvo(salvo)

    //User: retrieving game status
    case s@Status(_) =>
      println(playerBoard.board2String)
      println(enemyBoard.board2String)
      val turn = if (isMyTurn) user.userId else opponent.userId
      sender ! GameStatus(player, enemy, Game(Some(turn)))

    case Auto(_) =>
      auto = true
      sender ! Done
  }

  def player = UserBoard(user.userId, playerBoard.board.map(_.mkString).toList)

  def enemy = UserBoard(opponent.userId, enemyBoard.board.map(_.mkString).toList)

  def reduceDuplicates(l: List[(String, ShotResult)]) = {
    l.groupBy(_._1).map(x => x._1 -> x._2.map(_._2)).map { x =>
      x._1 -> (if (x._2.contains(ShotResult.Kill)) ShotResult.Kill
      else if (x._2.contains(Hit)) Hit
      else Miss)
    }
  }

  implicit class Regex(sc: StringContext) {
    def r = new util.matching.Regex(sc.parts.mkString, sc.parts.tail.map(_ => "x"): _*)
  }

  def cannons = rule match {
    case "standard" | "super-charge" => playerBoard.shipsAlive
    case r"([1-9]|10)-shot" => rule.split("-").head.toInt
    case "desperation" => 1 + playerBoard.shipsKilled
  }
}

object SpaceGame {

  def props(user: User, opponent: User, gameId: String, remote: SpaceshipProtocol, isFirst: Boolean, rule: String, webClient: WebClient) =
    Props(classOf[SpaceGame], user, opponent, gameId, remote, isFirst, rule, webClient)

  case class SalvoError(salvo: SalvoReq, t: Throwable)

  case class SalvoResponse(salvo: Map[String, ShotResult], game: Game)

  case class Game(playerTurn: Option[String], won: Option[String] = None)

  case class NotEnoughCannons(cannonsAvailable: Int)

  case object WrongTurn

  case object Done

}
