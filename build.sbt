name := """akka-http-stomp"""

version := "1.0.0"

scalaVersion := "2.12.8"

scalacOptions := Seq("-feature", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaV = "2.5.21"
  val akkaHttpV = "10.1.7"
  val parboiledV = "2.1.6"
  val scalamockV = "4.1.0"
  
  Seq(
    "com.typesafe.akka" %% "akka-http-core" % akkaHttpV,
    "com.typesafe.akka" %% "akka-stream" % akkaV,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV,
    "org.parboiled" %% "parboiled" % parboiledV,

    "com.typesafe.akka" %% "akka-testkit" % akkaV % Test,
    "com.typesafe.akka" %% "akka-stream-testkit" % akkaV % Test,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpV % Test,
    "org.scalatest" %% "scalatest" % "3.0.6" % Test,
    "org.scalamock" %% "scalamock" % scalamockV % Test
  )
}