package processing

import akka.actor.{Actor, ActorRef}
import models.{NewGameRequest, User}

import scala.util.Random

class Receptionist extends Actor {

  import Receptionist._

  var counter = 0l
  var games = Map.empty[String, ActorRef]
  val rand = Random

  def receive = awaitingForUser

  def active(user: User): Receive = {
    case g@NewGameRequest(opponentId, _, _, rules) =>
      val gameId = "game_" + counter
      counter += 1

      val starting = if (rand.nextBoolean()) user.userId else opponentId

      val spaceGame = context.actorOf(SpaceGame.props(user, g, gameId, starting == user.userId, rules))
      games += gameId -> spaceGame

      sender ! GameCreated(gameId, starting)

    case s@Salvo(gameId, _) =>
      games.get(gameId) match {
        case Some(pl) => pl forward s
        case None => sender ! NoSuchGame(gameId)
      }

    case f@Fire(gameId, _) =>
      games.get(gameId) match {
        case Some(ref) =>
          ref forward f
        case None => sender ! NoSuchGame(gameId)
      }

    case LogOut => context.unbecome()
  }

  def awaitingForUser: Receive = {
    case u@User(_, _) => context.become(active(u))
    case _ => sender ! NoUser
  }

}

object Receptionist {

  case object NoUser

  case object LogOut

  case class NoSuchGame(gameId: String)

  case class GameCreated(gameId: String, starting: String)

  case class Salvo(gameId: String, salvo: List[String])

  case class Fire(gameId: String, salvo: List[String])

}