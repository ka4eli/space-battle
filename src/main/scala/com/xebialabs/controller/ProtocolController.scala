package com.xebialabs.controller

import _root_.akka.actor.{ActorRef, ActorSystem}
import _root_.akka.pattern.ask
import _root_.akka.util.Timeout
import com.xebialabs.models.{NewGameRequest, NewGameResponse, User}
import com.xebialabs.processing.Shooter.Salvo
import org.scalatra._
import com.xebialabs.processing.Receptionist.{GameCreated, NoSuchGame, NoUser, SalvoReq}
import com.xebialabs.processing.SpaceGame.{WrongTurn, SalvoError, SalvoResponse}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class ProtocolController(receptionist: ActorRef, system: ActorSystem) extends ScalatraServlet with JsonSupport with FutureSupport {

  protected implicit def executor: ExecutionContext = system.dispatcher

  implicit val timeout = new Timeout(2 seconds)

  put("/game/:id") {
    val gameId = params("id")
    val salvo = parsedBody.extract[Salvo].salvo

    receptionist ? SalvoReq(gameId, salvo) map {
      case s@SalvoResponse(res, game) => s
      case SalvoError(s, e) => BadRequest(e.getMessage)
      case NoSuchGame(id) => NotFound(s"There is no active game with id $id")
      case WrongTurn => NotFound("It's not your turn to fire")
    }

  }


  post("/game/new") {
    val gameRequest = parsedBody.extract[NewGameRequest]

    receptionist ? gameRequest map {
      case ngr: NewGameResponse => Created(ngr)
      case NoUser => BadRequest("There is no currently authorized users")
    }
  }
}
