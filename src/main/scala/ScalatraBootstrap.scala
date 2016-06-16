import com.lthummus.sclmanager._
import org.scalatra._
import javax.servlet.ServletContext

import com.lthummus.sclmanager.database.DatabaseConfigurator
import org.jooq.DSLContext

class ScalatraBootstrap extends LifeCycle {

  implicit val dslContext = DatabaseConfigurator.getDslContext

  override def init(context: ServletContext) {
    context.mount(new SclManagerServlet, "/*")
  }
}
