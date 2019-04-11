import com.lthummus.sclmanager.scaffolding.SclManagerConfig
import org.eclipse.jetty.server.handler.RequestLogHandler
import org.eclipse.jetty.server.{CustomRequestLog, Server, Slf4jRequestLogWriter}
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra.servlet.ScalatraListener
import org.slf4j.LoggerFactory

object JettyLauncher {

  private val Logger = LoggerFactory.getLogger("JettyLauncher")
  
  def main(args: Array[String]) {
    Logger.info("Hello world!")
    Logger.info("Using log format {}", SclManagerConfig.logFormat)

    val port = SclManagerConfig.port

    val server = new Server(port)
    val context = new WebAppContext()
    context.setContextPath("/")
    context.setResourceBase(SclManagerConfig.webroot)
    context.addEventListener(new ScalatraListener)
    context.addServlet(classOf[DefaultServlet], "/")
    server.setHandler(context)


    val log = new Slf4jRequestLogWriter()
    log.setLoggerName("Jetty")

    server.setRequestLog(new CustomRequestLog(log, SclManagerConfig.logFormat))
    server.start()
    server.join()
  }
}
