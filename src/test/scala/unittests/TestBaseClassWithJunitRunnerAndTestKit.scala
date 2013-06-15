package unittests

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import akka.testkit.TestKit
import akka.actor.ActorSystem
import org.scalatest.FreeSpecLike
import org.scalatest.MustMatchers
import org.scalatest.mock.MockitoSugar

@RunWith(classOf[JUnitRunner])
abstract class TestBaseClassWithJunitRunnerAndTestKit extends TestKit(ActorSystem("testactorsystems"))
                                             with FreeSpecLike with StopSystemAfterAll with MustMatchers with MockitoSugar {

}
