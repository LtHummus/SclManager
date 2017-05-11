import com.typesafe.config.ConfigFactory
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra.servlet.ScalatraListener
import com.lthummus.sclmanager.scaffolding.SystemConfig._
import org.slf4j.LoggerFactory

object JettyLauncher {

  private val Logger = LoggerFactory.getLogger("JettyLauncher")

  def main(args: Array[String]) {
    Logger.info("Hello world!")
    val config = ConfigFactory.load()
    val port = config.getIntWithStage("server.port")

    val server = new Server(port)
    val context = new WebAppContext()
    context.setContextPath("/")
    context.setResourceBase(config.getStringWithStage("server.webroot"))
    context.addEventListener(new ScalatraListener)
    context.addServlet(classOf[DefaultServlet], "/")

    server.setHandler(context)


    server.start()
    server.join()
  }
}
