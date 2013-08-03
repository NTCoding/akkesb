name := "Jobs catalogue service for akkesb demo app"

scalaVersion := "2.10.1"

resolvers += "spray repo" at "http://nightlies.spray.io/"

libraryDependencies += "io.spray" % "spray-client" % "1.2-20130710"

libraryDependencies += "io.spray" % "spray-routing" % "1.2-20130710"

libraryDependencies += "com.typesafe.akka" % "akka-actor_2.10" % "2.2.0"

libraryDependencies += "com.sksamuel.elastic4s" % "elastic4s" % "0.90.2.8"

libraryDependencies += "com.cloudphysics" % "jerkson_2.10" % "0.6.3"




