ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.5.0"

lazy val composerr = (project in file("composerr"))
    .settings(
        name := "plascevo",
        idePackagePrefix := Some("cl.ravenhill.composerr"),
        libraryDependencies ++= Seq(
            "org.scalactic" %% "scalactic" % "3.2.19",
            "org.scalatest" %% "scalatest" % "3.2.19" % "test",
            "org.scalatestplus" %% "scalacheck-1-18" % "3.2.19.0" % "test"
        )
    )

lazy val testUtils = (project in file("test-utils"))
    .settings(
        name := "plascevo",
        idePackagePrefix := Some("cl.ravenhill.plascevo"),
        libraryDependencies ++= Seq(
            "org.scalameta" %% "munit" % "1.0.0",
            "com.typesafe.akka" %% "akka-actor-typed" % "2.8.6",
            "com.typesafe.akka" %% "akka-stream" % "2.8.6",
        )
    )
    .dependsOn(composerr)

lazy val proptest = (project in file("proptest"))
    .settings(
        name := "plascevo",
        idePackagePrefix := Some("cl.ravenhill.plascevo"),
        libraryDependencies ++= Seq(
            "org.scalameta" %% "munit" % "1.0.0",
            "com.typesafe.akka" %% "akka-actor-typed" % "2.8.6",
            "com.typesafe.akka" %% "akka-stream" % "2.8.6",
            "org.scalameta" %% "munit-scalacheck" % "0.7.29"
        )
    )
    .dependsOn(composerr)
    .dependsOn(testUtils)

lazy val plascevoCore = (project in file("plascevo-core"))
    .settings(
        name := "plascevo",
        idePackagePrefix := Some("cl.ravenhill.plascevo"),
        libraryDependencies ++= Seq(
            "org.scalactic" %% "scalactic" % "3.2.19",
            "org.scalatest" %% "scalatest" % "3.2.19" % "test",
            "org.scalatestplus" %% "scalacheck-1-18" % "3.2.19.0" % "test",
            "org.jfree" % "jfreechart" % "1.5.5",
            "com.typesafe.akka" %% "akka-actor-typed" % "2.8.6",
            "com.typesafe.akka" %% "akka-stream" % "2.8.6",
            "ch.qos.logback" % "logback-classic" % "1.5.7",
            "org.scalameta" %% "munit" % "1.0.0" % "test",
        )
    )
    .dependsOn(composerr)
    .dependsOn(testUtils % "test->test")
    .dependsOn(proptest % "test->test")

lazy val plascevoGenetics = (project in file("plascevo-genetics"))
    .settings(
        name := "plascevo",
        idePackagePrefix := Some("cl.ravenhill.plascevo"),
        libraryDependencies ++= Seq(
            "org.scalactic" %% "scalactic" % "3.2.19",
            "org.scalatest" %% "scalatest" % "3.2.19" % "test",
            "org.scalatestplus" %% "scalacheck-1-18" % "3.2.19.0" % "test",
        )
    )
    .dependsOn(plascevoCore)

lazy val examples = (project in file("examples"))
    .settings(
        name := "plascevo",
        idePackagePrefix := Some("cl.ravenhill.plascevo"),
    )
    .dependsOn(plascevoGenetics)
