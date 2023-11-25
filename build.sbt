name := """allariamas-live-peris"""
organization := "allariamas"
version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.12"

libraryDependencies += guice
libraryDependencies += jdbc
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.0" % Test
libraryDependencies += "com.typesafe.slick" %% "slick-hikaricp" % "3.3.3"
libraryDependencies += "com.typesafe.slick" %% "slick" % "3.3.3"
libraryDependencies += "org.postgresql" % "postgresql" % "42.3.2"


// Adds additional packages into Twirl
//TwirlKeys.templateImports += "allariamas.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "allariamas.binders._"
