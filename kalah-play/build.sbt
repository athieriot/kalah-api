name := """kalah-play"""
organization := "com.github.athieriot"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  guice,
  "org.assertj" % "assertj-core" % "3.8.0" % "test",
  "com.revinate" % "assertj-json" % "1.0.1" % "test"
)