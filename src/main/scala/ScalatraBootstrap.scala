import javax.servlet.ServletContext

import _root_.akka.actor.ActorSystem
import com.xebialabs.controller.{ProtocolController, UserController}
import com.xebialabs.models.User
import com.xebialabs.processing.{AsyncWebClient, Receptionist}
import org.scalatra._

import scala.util.Random

class ScalatraBootstrap extends LifeCycle {
  val system = ActorSystem("XL-spaceship")
  val receptionist = system.actorOf(Receptionist.props(AsyncWebClient))

  val id = Random.nextInt()
  receptionist ! User("userId" + id, "name" + id)

  override def init(context: ServletContext) {
    context.mount(new ProtocolController(receptionist, system), "/xl-spaceship/protocol/*", "protocol")
    context.mount(new UserController(receptionist, system), "/xl-spaceship/user/*", "user")
  }

  override def destroy(context: ServletContext) {
    system.shutdown()
  }
}
