import com.xebialabs.config.Configuration
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra.servlet.ScalatraListener


object JettyLauncher {
  def main(args: Array[String]) {
    val port = Configuration.port

    val server = new Server(port)
    val context = new WebAppContext()
    context setContextPath "/"
    context.setResourceBase("src/main/resources")
    context.addEventListener(new ScalatraListener)
    context.addServlet(classOf[DefaultServlet], "/")
    context.setWelcomeFiles(Array("index.html"))

    server.setHandler(context)

    server.start
    server.join
  }
}