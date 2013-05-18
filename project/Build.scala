import sbt._
import Keys._
import com.typesafe.sbt.SbtMultiJvm
import com.typesafe.sbt.SbtMultiJvm.MultiJvmKeys.{ MultiJvm }

object akkesbBuild extends Build {

    lazy val buildSettings = Defaults.defaultSettings ++ multiJvmSettings ++ Seq(
        organization := "ntcoding",
        version := "0.1",
        scalaVersion := "2.10.1",
        crossPaths := false
    )

    lazy val example = Project(
        id = "example",
        base = file("."),
        settings = buildSettings ++
            Seq(libraryDependencies ++= Dependencies.all)
    ) configs(MultiJvm)

    lazy val multiJvmSettings = SbtMultiJvm.multiJvmSettings ++ Seq(
        // make sure that MultiJvm test are compiled by the default test compilation
        compile in MultiJvm <<= (compile in MultiJvm) triggeredBy (compile in Test),
        // disable parallel tests
        parallelExecution in Test := false,
        // make sure that MultiJvm tests are executed by the default test target
        executeTests in Test <<=
            ((executeTests in Test), (executeTests in MultiJvm)) map {
                case ((_, testResults), (_, multiJvmResults))  =>
                    val results = testResults ++ multiJvmResults
                    (Tests.overall(results.values), results)
            }
    )

    object Dependencies {
         val all = Seq(
             // ---- application dependencies ----
             "com.typesafe.akka"  %% "akka-actor" % "2.1.4" ,
             "com.typesafe.akka"  %% "akka-remote" % "2.1.4" ,

             // ---- test dependencies ----
             "com.typesafe.akka" %% "akka-testkit" % "2.1.4" %
                 "test" ,
             "com.typesafe.akka" %% "akka-remote-tests-experimental" % "2.1.4" %
                 "test" ,
             "org.scalatest"     %% "scalatest" % "1.9.1" % "test",
             "junit" % "junit" % "4.10" % "test",
             "com.novocode" % "junit-interface" % "0.8" % "test"
         )
    }
}