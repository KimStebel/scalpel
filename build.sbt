name := "scalpel"

version := "1.0"

scalaVersion := "2.11.6"

scalacOptions ++= Seq(
    "-deprecation",
    "-encoding", "UTF-8",
    "-feature",
    "-target:jvm-1.7",
    "-language:_",
    "-unchecked",
    "-Ywarn-adapted-args",
    "-Ywarn-value-discard",
    "-Xlint"
)

libraryDependencies ++= Seq(
  "com.twitter" %% "finagle-httpx" % "6.27.0",
  "com.twitter" %% "util-core" % "6.26.0",
  "io.spray" %%  "spray-json" % "1.3.2",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
  "ch.qos.logback" % "logback-classic" % "0.9.28",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "org.mockito" % "mockito-all" % "1.8.1" % "test"
)

// set correct java version
javacOptions ++= Seq("-source", "1.7", "-target", "1.7")

fork in run := true

// Resolver
resolvers ++= Seq(
    "webjars" at "http://webjars.github.com/m2",
    "Local Maven Repository" at "file:///" + Path.userHome.absolutePath + "/.m2/repo",
    Resolver.file("Local Repository", file(sys.env.get("PLAY_HOME").map(_ + "/repository/local").getOrElse("")))(Resolver.ivyStylePatterns),
    Resolver.url("play-plugin-releases", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns),
    Resolver.url("play-plugin-snapshots", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-snapshots/"))(Resolver.ivyStylePatterns)
)

licenses := Seq("Apache v2.0" -> url("http://opensource.org/licenses/Apache-2.0"))

homepage := Some(url("https://github.com/kimstebel/scaspell"))

Revolver.settings

mainClass in assembly := Some("scaspell.Server")

assemblyJarName in assembly := "scaspell.jar"
