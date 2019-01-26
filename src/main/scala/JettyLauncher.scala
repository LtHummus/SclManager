import com.lthummus.sclmanager.scaffolding.SclManagerConfig
import com.typesafe.config.ConfigFactory
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra.servlet.ScalatraListener
import org.slf4j.LoggerFactory

object JettyLauncher {

  private val Logger = LoggerFactory.getLogger("JettyLauncher")

  def main(args: Array[String]) {
    Logger.info("Hello world!")

    val port = SclManagerConfig.port

    val server = new Server(port)
    val context = new WebAppContext()
    context.setContextPath("/")
    context.setResourceBase(SclManagerConfig.webroot)
    context.addEventListener(new ScalatraListener)
    context.addServlet(classOf[DefaultServlet], "/")

    server.setHandler(context)


    server.start()
    server.join()
  }
}
