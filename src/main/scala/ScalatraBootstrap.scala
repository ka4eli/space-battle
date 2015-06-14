import javax.servlet.ServletContext

import _root_.akka.actor.{ActorSystem, Props}
import controller.{ProtocolController, UserController}
import org.scalatra._
import processing.Receptionist

class ScalatraBootstrap extends LifeCycle {
  val system = ActorSystem("XL-spaceship")
  val receptionist = system.actorOf(Props[Receptionist])


  override def init(context: ServletContext) {
    context.mount(new ProtocolController(receptionist, system), "/xl-spaceship/protocol/*", "protocol")
    context.mount(new UserController(receptionist, system), "/xl-spaceship/user/*", "user")
  }

  override def destroy(context: ServletContext) {
    system.shutdown()
  }
}
