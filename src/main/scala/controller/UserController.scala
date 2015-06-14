package controller

import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout
import models.User
import org.scalatra.{BadRequest, FutureSupport, NotFound, ScalatraServlet}
import processing.Receptionist.{Fire, NoSuchGame}
import processing.SpaceGame.{SalvoResponse, WrongTurn}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class UserController(receptionist: ActorRef, system: ActorSystem) extends ScalatraServlet with JsonSupport with FutureSupport {

  protected implicit def executor: ExecutionContext = system.dispatcher

  implicit val timeout = new Timeout(500 milliseconds)

  val user = User("user_id", "name") //todo

  //todo implement auth

  get("/") {
    "Test!!"
  }

  post("/game/:id/auto") {
    //autopilot!
  }

  get("/game/:id") {
    val gameId = params("id")


    //    {
    //      "self": {
    //        "user_id": "player-1",
    //        "board": [...]},
    //      "opponent": {
    //        "user_id": "xebialabs-1",
    //        "board": []},
    //      "game": {
    //        "player_turn": "player-1"
    //      }
    //    }
  }
  put("/game/:id/fire") {

    val gameId = params("id")
    val salvo = parsedBody.extract[List[String]]
    receptionist ? Fire(gameId, salvo) map {
      case s@SalvoResponse(_, _) => s
      case NoSuchGame(gid) => NotFound(s"There is no game with id $gid")
      case WrongTurn => NotFound("It's not your turn to fire")
      case body: String => BadRequest(body)
    }

    //    {
    //      "salvo": ["0x0", "8x4", "DxA", "AxA", "7xF"]
    //    }
    //    Response
    //    JSON
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
    //      }
  }

  post("/game/new") {

    //    Request: POST /xl-spaceship/user/game/new
    //        JSON
    //    {
    //      "spaceship_protocol": {
    //        "hostname": "10.10.0.2",
    //        "port": 9000
    //      },
    //      "rules": "super-charge"
    //    }
    //    Success Response
    //      Status: 303 (See Other)
    //    Location: /xl-spaceship/user/game/<game_id>
    //    Content-Type: text/html
    //    A new game has been created at xl-spaceship/user/game/<game_id>

  }

}
