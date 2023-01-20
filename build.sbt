ThisBuild / organization := "one.estrondo"
ThisBuild / scalaVersion := "3.2.1"
ThisBuild / version      := "1.0.0"

ThisBuild / scalacOptions ++= Seq(
  "-explain"
)

publishMavenStyle      := true
ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"
ThisBuild / publishTo              := sonatypePublishToBundle.value
ThisBuild / licenses               := Seq("GPLv3" -> url("https://www.gnu.org/licenses/gpl-3.0.txt"))
ThisBuild / homepage               := Some(url("https://github.com/estrondo/sweet-mockito"))
ThisBuild / scmInfo                := Some(
  ScmInfo(
    url("https://github.com/estrondo/sweet-mockito"),
    "scm:git@github.com:estrondo/sweet-mockito.git"
  )
)
ThisBuild / developers             := List(
  Developer(
    id = "rthoth",
    name = "Ronaldo Silva",
    email = "ronaldo.asilva@gmail.com",
    url = url("https://github.com/rthoth")
  )
)

lazy val root = (project in file("."))
  .settings(
    name      := "sweet-mockito-root",
    publishTo := sonatypePublishToBundle.value
  )
  .aggregate(core, zio)

lazy val core = (project in file("core"))
  .settings(
    name := "sweet-mockito",
    libraryDependencies ++= Seq(
      "org.mockito"    % "mockito-core"             % "5.0.0",
      "org.scalatest" %% "scalatest-funspec"        % "3.2.15" % Test,
      "org.scalatest" %% "scalatest-shouldmatchers" % "3.2.15" % Test
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
