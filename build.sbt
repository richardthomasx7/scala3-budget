val scala3Version = "3.3.1"
val circeVersion = "0.14.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "budget-scala3",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test,
    libraryDependencies += "com.softwaremill.sttp.client4" %% "core" % "4.0.0-M6",
    libraryDependencies += "org.typelevel" %% "cats-effect" % "3.5.2",
    libraryDependencies += "org.typelevel" %% "cats-core" % "2.10.0",
    libraryDependencies += "com.softwaremill.sttp.client4" %% "circe" % "4.0.0-M6",
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-parser"
    ).map(_ % circeVersion),
    libraryDependencies += "com.softwaremill.sttp.client4" %% "cats" % "4.0.0-M6",
    libraryDependencies +=   "org.typelevel" %% "cats-time"     % "0.5.1"
  )
