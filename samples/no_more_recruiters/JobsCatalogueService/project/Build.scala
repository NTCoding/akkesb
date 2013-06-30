import akka.sbt.AkkaKernelPlugin
import org.apache.commons.io.FileUtils
import org.apache.ivy.util.FileUtil
import sbt._
import Keys._
import akka.sbt.AkkaKernelPlugin._

object jobCatBuild extends Build {

    lazy val buildSettings = Defaults.defaultSettings ++ Seq(
        scalaVersion := "2.10.1",
        crossPaths := false
    )

    lazy val proj = Project(
        id = "JobsCatalogueService",
        base = file("."),
        settings = buildSettings ++ AkkaKernelPlugin.distSettings ++ Seq(startHostAndAppTask)
                   ++ Seq(outputDirectory in Dist := file("target/JobsCatalogueService"))
    )

    lazy val startHostAndApp = TaskKey[Unit]("startHostAndApp", "Create distribution and copy configs")
    val startHostAndAppTask = startHostAndApp <<= dist map {d =>
      file("target/akkesb").delete()

      println("copying over akkesb distribution")
      FileUtils.copyDirectory(file("../../../target/akkesb"), file("target/akkesb"))
      file("target/akkesb/akkesb_startup.sh").setExecutable(true)
      file("target/akkesb/bin/start").setExecutable(true)

      println("copying akkesb.conf into akkesb disribution")
      IO.copyFile(file("akkesb.conf"), file("target/akkesb/akkesb.conf"))

      println("About to start akkesb")
      println( Process("sh", Seq("target/akkesb/akkesb_startup.sh", "&")).!!)

      println("starting this app")
      run
      d
    }

}