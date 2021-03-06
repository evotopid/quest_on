import sbt._
import Keys._
import org.scalatra.sbt._
import org.scalatra.sbt.PluginKeys._
import com.earldouglas.xwp.JettyPlugin
import com.mojolly.scalate.ScalatePlugin._
import com.typesafe.sbt.packager.archetypes.JavaAppPackaging
import ScalateKeys._

object Quest_onBuild extends Build {
  val Organization = "com.leoschwarz"
  val Name = "quest_on"
  val Version = "2.0.0-SNAPSHOT"
  val ScalaVersion = "2.11.8"
  val ScalatraVersion = "2.4.1"

  lazy val project = Project (
    "quest_on",
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
        "org.scalatra" %% "scalatra-json" % ScalatraVersion,
        "org.scalatra" %% "scalatra-scalate" % ScalatraVersion,
        "org.scalatra" %% "scalatra-auth" % ScalatraVersion,
        "org.scalatra" %% "scalatra-specs2" % ScalatraVersion % "test",
        "ch.qos.logback" % "logback-classic" % "1.1.5" % "runtime",
        "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided",
        "org.json4s" %% "json4s-jackson" % "3.4.0",

        // Jetty Server
        "org.eclipse.jetty" % "jetty-webapp" % "9.2.15.v20160210" % "compile;container",
        "org.eclipse.jetty" % "jetty-plus"   % "9.2.15.v20160210" % "compile;container",

        // YAML
        "org.yaml" % "snakeyaml" % "1.17",

        // Database
        "org.postgresql" % "postgresql" % "9.4.1209",
        "org.xerial" % "sqlite-jdbc" % "3.8.11.2",

        // Excel format support
        "org.apache.poi" % "poi" % "3.14",
        "org.apache.poi" % "poi-ooxml" % "3.14"
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
  ).enablePlugins(JettyPlugin, JavaAppPackaging)
}
