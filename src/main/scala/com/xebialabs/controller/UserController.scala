package com.xebialabs.controller

import _root_.akka.actor.{ActorRef, ActorSystem}
import _root_.akka.pattern.ask
import _root_.akka.util.Timeout
import com.xebialabs.models.{ChallengeGame, NewGameResponse}
import com.xebialabs.processing.AsyncWebClientError
import com.xebialabs.processing.Receptionist._
import com.xebialabs.processing.Shooter.Salvo
import com.xebialabs.processing.SpaceGame._
import org.scalatra._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class UserController(receptionist: ActorRef, system: ActorSystem)
  extends ScalatraServlet with JsonSupport with FutureSupport with CorsSupport {

  protected implicit def executor: ExecutionContext = system.dispatcher

  implicit val timeout = new Timeout(2 seconds)

  //todo implement auth

  get("/games") {
    receptionist ? GetGames map {
      case GamesList(games) => games
    }
  }

  get("/game/:id") {
    val gameId = params("id")

    receptionist ? Status(gameId) map {
      case g@GameStatus(_, _, _) => g
      case NoSuchGame(gid) => NotFound(s"There is no game with id $gid")
    }
  }

  put("/game/:id/fire") {
    val gameId = params("id")
    val salvo = parsedBody.extract[Salvo].salvo

    receptionist ? Fire(gameId, salvo) map {
      case s@SalvoResponse(_, _) => s
      case WrongTurn => NotFound("It's not your turn to fire")
      case NotEnoughCannons(i) => BadRequest(s"Wrong salvo size: ${salvo.size}. You can perform no more than $i shoots per salvo")
      case NoSuchGame(gid) => NotFound(s"There is no game with id $gid")
      case AsyncWebClientError => InternalServerError
      case body: String => BadRequest(body)
    }
  }

  post("/game/new") {
    val challenge = parsedBody.extract[ChallengeGame]

    receptionist ? challenge map {
      case g@NewGameResponse(_, _, gid, _, _) =>
        val headers = Map("Content-Type" -> "text/html")
        val location = s"/xl-spaceship/user/game/$gid"
        SeeOther(location, headers, s"A new game has been created at $location")
      case body: String => BadRequest(body)
    }
  }

  post("/game/:id/auto") {
    val gameId = params("id")

    receptionist ? Auto(gameId) map {
      case NoSuchGame(gid) => NotFound(s"There is no game with id $gid")
      case Done => Ok
    }
  }

  options("/*") {
    response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"))
  }

}
