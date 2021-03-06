import com.lthummus.sclmanager._
import org.scalatra._
import javax.servlet.ServletContext
import com.lthummus.sclmanager.database.DatabaseConfigurator
import com.lthummus.sclmanager.servlets._
import org.jooq.DSLContext

class ScalatraBootstrap extends LifeCycle {

  implicit val dslContext = DatabaseConfigurator.getDslContext

  implicit val swagger = new SclSwagger

  override def init(context: ServletContext) {
//    context.mount(new SclManagerServlet, "/*")
    context.mount(new DownloadServlet, "/download")
    context.mount(new LeagueServlet, "/api/league/*", "league")
    context.mount(new MatchServlet, "/api/match/*")
    context.mount(new PlayerServlet, "/api/player/*")
    context.mount(new DraftServlet, "/api/draft/*")
    context.mount(new ResourcesApp, "/api-docs")
    context.mount(new StatsServlet, "/api/stats/*")
    context.mount(new ChallengerUtils, "/api/challenger/*")
  }
}
