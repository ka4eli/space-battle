package controller

import _root_.akka.actor.{ActorRef, ActorSystem}
import _root_.akka.pattern.ask
import _root_.akka.util.Timeout
import models.{NewGameRequest, NewGameResponse, User}
import org.scalatra._
import processing.Receptionist.{GameCreated, NoSuchGame, NoUser, Salvo}
import processing.SpaceGame.{WrongTurn, SalvoError, SalvoResponse}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class ProtocolController(receptionist: ActorRef, system: ActorSystem) extends ScalatraServlet with JsonSupport with FutureSupport {

  protected implicit def executor: ExecutionContext = system.dispatcher

  val user = User("user_id", "name") //todo

  implicit val timeout = new Timeout(500 milliseconds)

  get("/") {
    "Success!"
  }

  put("/game/:id") {
    val gameId = params("id")
    val salvo = parsedBody.extract[List[String]]
    val f = receptionist ? Salvo(gameId, salvo)

    f.map {
      case s@SalvoResponse(res, game) => s
      case SalvoError(s, e) => BadRequest(e.getMessage)
      case NoSuchGame(id) => NotFound(s"There is no game with id $id")
      case WrongTurn => NotFound("It's not your turn to fire")
    }
    //request
    //    {
    //      "salvo": ["0x0", "8x4", "DxA", "AxA", "7xF"]
    //    }

    //response
    //    {
    //      "salvo": {
    //        "0x0": "hit",
    //        "8x4": "hit",
    //        "DxA": "kill",
    //        "AxA": "miss",
    //        "7xF": "miss"
    //      },
    //      "game": {
    //        "player_turn": "player-1"
    //      }  OR
    //      "game": {
    //        "won": "xebialabs-1"
    //      }
    //
    //    }

  }


  post("/game/new") {
    val gameRequest = parsedBody.extract[NewGameRequest]

    new AsyncResult {
      val is = receptionist ? gameRequest map {
        case GameCreated(gameId, starting) => Created(NewGameResponse(user.userId, user.fullName, gameId, starting, gameRequest.rules))
        case NoUser => BadRequest("There is no currently authorized users")
      }
    }

    //        {
    //          "user_id": "xebialabs-1",
    //          "full_name": "XebiaLabs Opponent",
    //          "rules": "6-shot",
    //          "spaceship_protocol": {
    //            "hostname": "127.0.0.1",
    //            "port": 9001
    //          }
    //        }
    //    Response: Http Status 201 (Created)
    //    JSON
    //    {
    //      "user_id": "player",
    //      "full_name": "Assessment Player",
    //      "game_id": "match-1",
    //      "starting": "xebialabs-1",
    //      "rules": "6-shot"
    //    }
  }
}
