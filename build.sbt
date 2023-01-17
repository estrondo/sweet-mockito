ThisBuild / organization := "one.estrondo"
ThisBuild / scalaVersion := "3.2.1"
ThisBuild / version      := "1.0.0"

ThisBuild / scalacOptions ++= Seq(
  "-explain"
)

lazy val root = (project in file("."))
  .settings(
    name := "sweet-mockito-root"
  )
  .aggregate(core, zio)

lazy val core = (project in file("core"))
  .settings(
    name := "sweet-mockito",
    libraryDependencies ++= Seq(
      "org.mockito"    % "mockito-core" % "5.0.0",
      "org.scalatest" %% "scalatest-funspec" % "3.2.15" % Test
    )
  )

lazy val zio = (project in file("zio"))
  .settings(
    name := "sweet-mockito-zio",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio"          % "2.0.5",
      "dev.zio" %% "zio-test"     % "2.0.5" % Test,
      "dev.zio" %% "zio-test-sbt" % "2.0.5" % Test
    )
  )
  .dependsOn(core)
