name := "careers data importer"

scalaVersion := "2.10.1"

resolvers += "spray repo" at "http://nightlies.spray.io/"

libraryDependencies += "io.spray" % "spray-client" % "1.2-20130710"

libraryDependencies += "com.typesafe.akka" % "akka-actor_2.10" % "2.2.0"

libraryDependencies += "com.cloudphysics" % "jerkson_2.10" % "0.6.3"

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.0.M6-SNAP17"

libraryDependencies += "org.jsoup" % "jsoup" % "1.7.2"
