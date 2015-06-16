package com.xebialabs.processing

import akka.actor.{Props, Actor, ActorRef, PoisonPill}
import com.xebialabs.config.Configuration
import com.xebialabs.models._
import com.xebialabs.processing.SpaceGame.Game
import org.slf4j.LoggerFactory

import scala.util.Random

class Receptionist(webClient: WebClient) extends Actor {
  private val log = LoggerFactory.getLogger(classOf[Receptionist])

  import Receptionist._

  var games = Map.empty[String, ActorRef]
  var finishedGames = Map.empty[String, GameStatus]
  val rand = Random
  var counter = rand.nextInt(10000) //todo

  val rulesRegex = List("standard", "desperation", "super-charge", "([1-9]|10)-shot")

  def receive = awaitingForUser

  def active(user: User): Receive = {

    case g@NewGameRequest(oid, ofn, remote, rules) =>
      //todo make unique
      val gameId = "game_" + counter
      counter += 1

      val starting = if (rand.nextBoolean()) user.userId else oid

      val spaceGame = context.actorOf(SpaceGame.props(user, User(oid, ofn), gameId, remote,
        starting == user.userId, getRules(rules), webClient))
      log.info(s"New game $gameId with rules [${getRules(rules)}] started between ${user.userId} and $oid. Starting is $starting")
      games += gameId -> spaceGame

      sender ! NewGameResponse(user.userId, user.fullName, gameId, starting, rules)

    //Protocol: i'm attacked
    case s@SalvoReq(gameId, _) =>
      games.get(gameId) match {
        case Some(pl) => pl forward s
        case None => sender ! NoSuchGame(gameId)
      }

    //SpaceGame: getting message about finishing game
    case (gid: String, gameStatus: GameStatus) if gameStatus.game.won.isDefined =>
      finishedGames += gid -> gameStatus
      games -= gid
      sender ! PoisonPill

    //User: retrieving game status
    case s@Status(gameId) =>
      games.get(gameId) match {
        case Some(ref) => ref forward s
        case None => finishedGames.get(gameId) match {
          case Some(status) => sender ! status
          case None => sender ! NoSuchGame(gameId)
        }
      }

    //User: trying to challenge another player
    case ChallengeGame(remote, r) =>
      val req = NewGameRequest(user.userId, user.fullName, SpaceshipProtocol(Configuration.host, Configuration.port), r)
      context.actorOf(Challenger.props(remote, req, sender(), webClient)) ! req

    //User: trying to fire
    case f@Fire(gameId, _) =>
      games.get(gameId) match {
        case Some(ref) =>
          ref forward f
        case None => sender ! NoSuchGame(gameId)
      }

    //Challenger: received positive response for game challenging
    case (NewGameResponse(oid, ofn, gameId, starting, rules), remote: SpaceshipProtocol) =>
      val spaceGame = context.actorOf(SpaceGame.props(user, User(oid, ofn), gameId,
        remote, starting == user.userId, getRules(rules), webClient))
      log.info(s"New game $gameId with rules [${getRules(rules)}] started between ${user.userId} and $oid. Starting is $starting")
      games += gameId -> spaceGame

    case a@Auto(gameId) =>
      games.get(gameId) match {
        case Some(ref) => ref forward a
        case None => sender ! NoSuchGame(gameId)
      }

    case LogOut =>
      context.children.foreach(_ ! PoisonPill)
      games = Map()
      finishedGames = Map()
      context.unbecome()
  }

  def awaitingForUser: Receive = {
    case u: User => context.become(active(u))
    case _ => sender ! NoUser
  }

  def getRules(rules: Option[String]): String = rules match {
    case Some(x) if rulesRegex.exists(x.matches) => x
    case _ => "standard"
  }
}

object Receptionist {

  def props(webClient: WebClient) = Props(classOf[Receptionist], webClient)

  case object NoUser

  case object LogOut

  case class NoSuchGame(gameId: String)

  case class GameCreated(gameId: String, starting: String)

  case class SalvoReq(gameId: String, salvo: List[String])

  case class Fire(gameId: String, salvo: List[String])

  case class Status(gameId: String)

  case class GameStatus(self: UserBoard, opponent: UserBoard, game: Game)

  case class UserBoard(userId: String, board: List[String])

  case class Auto(gameId: String)

}