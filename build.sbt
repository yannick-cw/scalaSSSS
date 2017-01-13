import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.gladow",
      crossScalaVersions := Seq("2.12.1", "2.11.8"),
      version := "0.1.0-SNAPSHOT"
    )),
    name := "scalaSSSS",
    libraryDependencies ++= Seq(
      scalaCheck % Test,
      cats
    )
  )

resolvers += Resolver.sonatypeRepo("releases")

addCompilerPlugin("org.spire-math" % "kind-projector" % "0.9.3" cross CrossVersion.binary)
