import com.typesafe.sbt.pgp.PgpKeys
import sbt._
import sbt.Keys._

object Build extends Build {

  val org = "com.landoop.avro4s-ui"
  val appVersion = "1.0.0"

  val ScalaVersion = "2.11.7"
  val ScalatestVersion = "3.0.0-M12"
  val Slf4jVersion = "1.7.12"
  val Log4jVersion = "1.2.17"
  val Avro4sVersion = "1.1.3"
  val JettyVersion = "8.1.18.v20150929"

  val rootSettings = Seq(
    organization := org,
    scalaVersion := ScalaVersion,
    //crossScalaVersions := Seq(ScalaVersion, "2.12.0-M3"),
    publishMavenStyle := true,
    resolvers += Resolver.mavenLocal,
    publishArtifact in Test := false,
    parallelExecution in Test := false,
    scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8"),
    javacOptions := Seq("-source", "1.7", "-target", "1.7"),
    sbtrelease.ReleasePlugin.autoImport.releasePublishArtifactsAction := PgpKeys.publishSigned.value,
    sbtrelease.ReleasePlugin.autoImport.releaseCrossBuild := true,
    libraryDependencies ++= Seq(
      "com.sksamuel.avro4s" %% "avro4s-core"      % Avro4sVersion,
      "com.sksamuel.avro4s" %% "avro4s-generator" % Avro4sVersion,
      "com.sksamuel.avro4s" %% "avro4s-json"      % Avro4sVersion,
      "org.scala-lang"      % "scala-reflect"     % ScalaVersion,
      "org.slf4j"           % "slf4j-api"         % Slf4jVersion,
      "org.eclipse.jetty"   % "jetty-servlet"     % JettyVersion,
      "org.eclipse.jetty"   % "jetty-server"      % JettyVersion,
      "log4j"               % "log4j"             % Log4jVersion % "test",
      "org.slf4j"           % "log4j-over-slf4j"  % Slf4jVersion % "test",
      "org.scalatest"       %% "scalatest"        % ScalatestVersion % "test"
    ),
    publishTo <<= version {
      (v: String) =>
        val nexus = "https://oss.sonatype.org/"
        if (v.trim.endsWith("SNAPSHOT"))
          Some("snapshots" at nexus + "content/repositories/snapshots")
        else
          Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    pomExtra := {
      <url>https://github.com/landoop/avro4s-ui</url>
        <licenses>
          <license>
            <name>MIT</name>
            <url>https://opensource.org/licenses/MIT</url>
            <distribution>repo</distribution>
          </license>
        </licenses>
        <scm>
          <url>git@github.com:landoop/avro4s-ui.git</url>
          <connection>scm:git@github.com:landoop/avro4s-ui.git</connection>
        </scm>
        <developers>
          <developer>
            <id>antwnis</id>
            <name>Antonios Chalkiopoulos</name>
            <url>http://github.com/antwnis</url>
          </developer>
        </developers>
    }
  )

  lazy val root = Project("avro4s-ui", file("."))
    .settings(rootSettings: _*)
    .settings(publish := {})
    .settings(name := "avro4s-ui")

}