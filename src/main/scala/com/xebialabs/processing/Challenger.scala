package com.xebialabs.processing

import java.util.concurrent.Executor

import akka.actor.{Actor, ActorRef, Props, Status}
import akka.pattern.pipe
import com.ning.http.client.Response
import com.xebialabs.models.{NewGameRequest, NewGameResponse, SpaceshipProtocol}
import org.json4s.jackson.JsonMethods
import org.json4s.jackson.JsonMethods._

import scala.concurrent.ExecutionContext

class Challenger(remote: SpaceshipProtocol, gameReq: NewGameRequest, caller: ActorRef, webClient: WebClient) extends Actor {

  import org.json4s._
  import org.json4s.jackson.Serialization

  implicit val formats = Serialization.formats(NoTypeHints)
  implicit val executor = context.dispatcher.asInstanceOf[Executor with ExecutionContext]

  val body = JsonMethods.mapper.writeValueAsString(Extraction.decompose(gameReq)(formats).underscoreKeys)
  val url = s"http://${remote.hostname}:${remote.port}/xl-spaceship/protocol/game/new"

  webClient post(url, body) pipeTo self

  def receive = {
    case r: Response if r.getStatusCode == 201 =>
      val newGamerResponse = parse(r.getResponseBody).camelizeKeys.extract[NewGameResponse]
      context.parent ! ((newGamerResponse, remote))
      caller ! newGamerResponse
      context.stop(self)

    case r: Response =>
      caller ! r.getResponseBody
      context.stop(self)

    case e: Status.Failure =>
      caller ! AsyncWebClientError
      context.stop(self)
  }
}

object Challenger {

  def props(remote: SpaceshipProtocol, gameReq: NewGameRequest, caller: ActorRef, webClient: WebClient) =
    Props(classOf[Challenger], remote, gameReq, caller, webClient)

}
