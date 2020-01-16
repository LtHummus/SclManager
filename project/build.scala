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
  val ScalatraVersion = "2.6.5"

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
      resolvers += Resolver.sonatypeRepo("snapshots"),
      resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases",
      resolvers += "spray repo" at "http://repo.typesafe.com/typesafe/releases/",


        resolvers += "jda" at "https://dl.bintray.com/dv8fromtheworld/maven/",
      javaOptions ++= Seq("-Xdebug",
        "-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"),
      libraryDependencies ++= Seq(
        "org.scalatra" %% "scalatra" % ScalatraVersion,
        "org.scalatra" %% "scalatra-scalate" % ScalatraVersion,
        "org.scalatra" %% "scalatra-specs2" % ScalatraVersion % "test",
        "org.scalatra" %% "scalatra-swagger" % ScalatraVersion,
        "ch.qos.logback" % "logback-classic" % "1.1.11" % "runtime",
        "org.eclipse.jetty" % "jetty-webapp" % "9.4.15.v20190215" % "container;compile",
        "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided",

        "net.dv8tion" % "JDA" % "4.0.0_62" exclude("club.minnced", "opus-java"),


        // json support
        "org.scalatra" %% "scalatra-json" % ScalatraVersion,
        "org.json4s" %% "json4s-jackson" % "3.6.5",

        // i am so functional right now
        "org.scalaz" %% "scalaz-core" % "7.2.27",

        // apache is cool and good
        "commons-io" % "commons-io" % "2.6",

        // database bullshit
        "org.jooq" % "jooq" % "3.11.11",
        "org.jooq" % "jooq-codegen" % "3.11.11",
        "mysql" % "mysql-connector-java" % "5.1.40",
        "com.zaxxer" % "HikariCP" % "3.3.1",

        //aws
        "com.amazonaws" % "aws-java-sdk-s3" % "1.11.490",

        //dates are garbage
        "com.github.nscala-time" %% "nscala-time" % "2.22.0",

        //for discord postin'
        "org.scalaj" %% "scalaj-http" % "2.4.1"
      )
    )
  ).enablePlugins(JettyPlugin)
}
