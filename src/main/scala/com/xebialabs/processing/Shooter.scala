package com.xebialabs.processing

import java.util.concurrent.Executor

import akka.actor.{Actor, ActorRef, Props, Status}
import akka.pattern.pipe
import com.ning.http.client.Response
import com.xebialabs.json.ShootResultJsonSerializer
import com.xebialabs.models.SpaceshipProtocol
import com.xebialabs.processing.SpaceGame.SalvoResponse
import org.json4s.jackson.JsonMethods._

import scala.concurrent.ExecutionContext

class Shooter(remote: SpaceshipProtocol, gameId: String, webClient: WebClient) extends Actor {

  import com.xebialabs.processing.Shooter._
  import org.json4s._
  import org.json4s.jackson.Serialization.write

  implicit val formats = DefaultFormats.+(new ShootResultJsonSerializer)
  implicit val executor = context.dispatcher.asInstanceOf[Executor with ExecutionContext]

  val url = s"http://${remote.hostname}:${remote.port}/xl-spaceship/protocol/game/$gameId"

  def receive = {
    case s@Salvo(_) =>
      val caller = sender()
      val body = write(s)
      webClient put(url, body) map ((_, caller)) pipeTo self

    case (r: Response, caller: ActorRef) if r.getStatusCode == 200 =>
      val salvoResponse = parse(r.getResponseBody).camelizeKeys.extract[SalvoResponse]
      context.parent ! salvoResponse
      caller ! salvoResponse
      context.stop(self)

    case (r: Response, caller: ActorRef) =>
      caller ! r.getResponseBody
      context.stop(self)

    case (e: Status.Failure, caller: ActorRef) =>
      caller ! AsyncWebClientError
      context.stop(self)
  }
}

object Shooter {

  def props(remote: SpaceshipProtocol, gameId: String, webClient: WebClient) = Props(classOf[Shooter], remote, gameId, webClient)

  case class Salvo(salvo: List[String])

}