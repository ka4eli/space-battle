package processing

import akka.actor.{Actor, ActorRef}
import com.ning.http.client.Response
import dispatch.Defaults._
import dispatch._
import models.NewGameRequest
import org.json4s.jackson.JsonMethods._
import processing.Receptionist.Fire
import processing.SpaceGame.SalvoResponse

class Shooter(gameReq: NewGameRequest) extends Actor {

  import processing.Shooter._

  import org.json4s._
  import org.json4s.jackson.Serialization
  import org.json4s.jackson.Serialization.write

  implicit val formats = Serialization.formats(NoTypeHints)

  def receive = {
    case f@Fire(gid, salvo) =>
      val body = write(Salvo(salvo))
      process(sendShoot(body, gid), sender())
  }

  def sendShoot(body: String, gameId: String): Future[Response] = {
    val oppHost = host(gameReq.spaceshipProtocol.hostname, gameReq.spaceshipProtocol.port)
    val req = (oppHost / "xl-spaceship" / "protocol" / "game" / gameId).PUT
    req.setBody(body).setContentType("application/json", "UTF-8")
    Http(req)
  }

  def process(f: Future[Response], caller: ActorRef) {
    f.foreach { resp =>
      if (resp.getStatusCode == 200) {
        val salvoResponse = parse(resp.getResponseBody).extract[SalvoResponse]
        context.parent ! salvoResponse
        caller ! salvoResponse
      } else caller ! resp.getResponseBody
    }
  }

}

object Shooter {

  case class Salvo(salvo: List[String])

}