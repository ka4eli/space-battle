package com.xebialabs.client

import com.xebialabs.models.{ChallengeGame, SpaceshipProtocol}
import com.xebialabs.processing.Receptionist.{GameStatus, UserBoard}
import com.xebialabs.processing.Shooter.Salvo
import com.xebialabs.processing.SpaceGame.Game

object SpaceClient extends App {

  import com.ning.http.client.AsyncHttpClient
  import org.json4s._
  import org.json4s.jackson.Serialization
  import org.json4s.jackson.Serialization.write

  implicit val formats = Serialization.formats(FullTypeHints(List(classOf[GameStatus], classOf[UserBoard], classOf[Salvo], classOf[Game])))
  val asyncHttpClient = new AsyncHttpClient()


  //challenge remote player for game
  def sendChallenge(self: SpaceshipProtocol, remote: SpaceshipProtocol, rules: Option[String]): String = {
    val url = s"http://${self.hostname}:${self.port}/xl-spaceship/user/game/new"

    val challenge = ChallengeGame(remote, rules)

    val body = write(challenge)

    val res = asyncHttpClient.preparePost(url).setBody(body).execute().get()

    println(res.getStatusCode)
    println(res.getResponseBody)
    println(res.getStatusText)
    val gameId = res.getStatusText.split("/").last
    println("Game id is: " + gameId)
    gameId
  }

  def sendFire(self: SpaceshipProtocol, gid: String, salvo: Salvo) {
    val url = s"http://${self.hostname}:${self.port}/xl-spaceship/user/game/$gid/fire"
    val body = write(salvo)
    val res = asyncHttpClient.preparePut(url).setBody(body).execute().get()

    println(res.getStatusCode)
    println(res.getResponseBody)
    println(res.getStatusText)
  }

  def sendStatus(self: SpaceshipProtocol, gid: String) {
    val url = s"http://${self.hostname}:${self.port}/xl-spaceship/user/game/$gid"
    val res = asyncHttpClient.prepareGet(url).execute().get()

    println(res.getStatusCode)
    println(res.getResponseBody)
    println(res.getStatusText)

  }

  //send autopilot request
  def sendAuto(sp: SpaceshipProtocol, gid: String) = {
    val url = s"http://${sp.hostname}:${sp.port}/xl-spaceship/user/game/$gid/auto"
    val res = asyncHttpClient.preparePost(url).execute().get()
    println(res.getStatusCode)
  }

  val availableRules = List("standard", "desperation", "super-charge") ++ (1 to 10).map(_ + "-shot")
  println(availableRules)

  val player = SpaceshipProtocol("localhost", 8080)
  val opponent = SpaceshipProtocol("localhost", 8081)

  val gameId = sendChallenge(player, opponent, Some("standard"))

  sendAuto(opponent, gameId)
  //  sendAuto(player, gameId)

  //  val salvo = Salvo(List("5x1"))
  //  sendFire(player, gameId, salvo)
  //  sendStatus(player, gameId)


  asyncHttpClient.close()

}
