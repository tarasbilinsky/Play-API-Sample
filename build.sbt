import play.sbt.PlayInternalKeys
import play.sbt.PlayRunHook
import play.sbt.run.PlayRun

name := "static"
organization := "net.oltiv"
version := "0.0.1"

lazy val `api` = (project in file(".")).enablePlugins(PlayScala)

resolvers ++= Seq(
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
  "javaxt.com" at "http://www.javaxt.com/maven",
  "Maven central"  at "https://repo1.maven.org/maven2",
  Resolver.mavenLocal
)

libraryDependencies ++= Seq(guice,

  "org.scalamock" %% "scalamock-scalatest-support" % "3.6.0" % Test,
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
  "org.scalacheck" %% "scalacheck" % "1.13.5" % Test,
)

scalaVersion := "2.12.6"
scalacOptions ++= Seq("-feature","-unchecked","-Xlint:unsound-match","-deprecation","-Yno-adapted-args") //"-Ylog-classpath","-Xlog-implicits"
Compile / javacOptions ++= Seq("-Xlint:unchecked","-Xlint:deprecation")


Compile / doc / sources := Seq.empty
Compile / packageDoc / publishArtifact := false




