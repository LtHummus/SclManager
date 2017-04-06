import sbt._
import Keys._
import org.scalatra.sbt._
import org.scalatra.sbt.PluginKeys._
import com.earldouglas.xwp.JettyPlugin

import org.scalatra.sbt.DistPlugin._

object SclManagerBuild extends Build {
  val Organization = "com.lthummus"
  val Name = "sclmanager"
  val Version = "0.1.0-SNAPSHOT"
  val ScalaVersion = "2.12.1"
  val ScalatraVersion = "2.5.0"

  val myDistSettings = DistPlugin.distSettings ++ Seq(
    mainClass in Dist := Some("JettyLauncher")
  )

  lazy val project = Project (
    "scl-manager",
    file("."),
    settings = ScalatraPlugin.scalatraSettings ++ myDistSettings ++
      Seq(
      organization := Organization,
      name := Name,
      version := Version,
      scalaVersion := ScalaVersion,
      resolvers += Classpaths.typesafeReleases,
      resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases",
      javaOptions ++= Seq("-Xdebug",
        "-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"),
      libraryDependencies ++= Seq(
        "org.scalatra" %% "scalatra" % ScalatraVersion,
        "org.scalatra" %% "scalatra-scalate" % ScalatraVersion,
        "org.scalatra" %% "scalatra-specs2" % ScalatraVersion % "test",
        "org.scalatra" %% "scalatra-swagger" % ScalatraVersion,
        "ch.qos.logback" % "logback-classic" % "1.1.5" % "runtime",
        "org.eclipse.jetty" % "jetty-webapp" % "9.4.0.v20161208" % "container;compile",
        "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided",

        // json support
        "org.scalatra" %% "scalatra-json" % ScalatraVersion,
        "org.json4s" %% "json4s-jackson" % "3.5.1",

        // i am so functional right now
        "org.scalaz" %% "scalaz-core" % "7.2.10",

        // database bullshit
        "org.jooq" % "jooq" % "3.8.2",
        "mysql" % "mysql-connector-java" % "5.1.16",
        "com.zaxxer" % "HikariCP" % "2.4.6",

        // configuration
        "com.typesafe" % "config" % "1.3.0",

        //aws
        "com.amazonaws" % "aws-java-sdk" % "1.11.39",

        //dates are garbage
        "com.github.nscala-time" %% "nscala-time" % "2.16.0"
      )
    )
  ).enablePlugins(JettyPlugin)
}
