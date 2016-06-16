import sbt._
import Keys._
import org.scalatra.sbt._
import org.scalatra.sbt.PluginKeys._
import com.earldouglas.xwp.JettyPlugin
import com.mojolly.scalate.ScalatePlugin._
import ScalateKeys._

object SclManagerBuild extends Build {
  val Organization = "com.lthummus"
  val Name = "SCL Manager"
  val Version = "0.1.0-SNAPSHOT"
  val ScalaVersion = "2.11.8"
  val ScalatraVersion = "2.4.1"

  lazy val project = Project (
    "scl-manager",
    file("."),
    settings = ScalatraPlugin.scalatraSettings ++ scalateSettings ++ Seq(
      organization := Organization,
      name := Name,
      version := Version,
      scalaVersion := ScalaVersion,
      resolvers += Classpaths.typesafeReleases,
      resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases",
      libraryDependencies ++= Seq(
        "org.scalatra" %% "scalatra" % ScalatraVersion,
        "org.scalatra" %% "scalatra-scalate" % ScalatraVersion,
        "org.scalatra" %% "scalatra-specs2" % ScalatraVersion % "test",
        "ch.qos.logback" % "logback-classic" % "1.1.5" % "runtime",
        "org.eclipse.jetty" % "jetty-webapp" % "9.2.15.v20160210" % "container",
        "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided",

        // i am so functional right now
        "org.scalaz" %% "scalaz-core" % "7.2.3",

        // database bullshit
        "org.jooq" % "jooq" % "3.8.2",
        "mysql" % "mysql-connector-java" % "5.1.16",
        "com.zaxxer" % "HikariCP" % "2.4.6",

        // configuration
        "com.typesafe" % "config" % "1.3.0"

      ),
      scalateTemplateConfig in Compile <<= (sourceDirectory in Compile){ base =>
        Seq(
          TemplateConfig(
            base / "webapp" / "WEB-INF" / "templates",
            Seq.empty,  /* default imports should be added here */
            Seq(
              Binding("context", "_root_.org.scalatra.scalate.ScalatraRenderContext", importMembers = true, isImplicit = true)
            ),  /* add extra bindings here */
            Some("templates")
          )
        )
      }
    )
  ).enablePlugins(JettyPlugin)
}
