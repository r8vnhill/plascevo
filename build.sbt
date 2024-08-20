ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.5.0"

lazy val root = (project in file("."))
    .settings(
        name := "plascevo",
        idePackagePrefix := Some("cl.ravenhill.plascevo")
    )

libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.19"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % "test"
libraryDependencies += "org.scalatestplus" %% "scalacheck-1-18" % "3.2.19.0" % "test"
libraryDependencies += "org.jfree" % "jfreechart" % "1.5.5"