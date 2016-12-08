lazy val `akka-mit-niko` =
  project
    .in(file("."))
    .configs(MultiJvm)
    .enablePlugins(
//    AutomateHeaderPlugin,
      GitVersioning,
      JavaAppPackaging,
      DockerPlugin
    )

libraryDependencies ++= Vector(
  Library.akkaClusterSharding,
  Library.akkaContrib,
  Library.akkaDistributedData,
  Library.akkaHttp,
  Library.akkaHttpCirce,
  Library.akkaLog4j,
  Library.akkaPersistenceCassandra,
  Library.circeGeneric,
  Library.circeParser,
  Library.circeJava8,
  Library.log4jCore,
  Library.playJson,
  Library.akkaHttpTestkit % "test",
  Library.akkaMultiNodeTestkit % "test",
  Library.akkaTestkit % "test",
  Library.scalaTest % "test",
  Library.junit % "test"
)
