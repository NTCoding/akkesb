import scala.collection.mutable.ListBuffer
import scala.concurrent.{Future, ExecutionContext}
import ExecutionContext.Implicits.global

object MessageListener {

    def main(args: Array[String]) {
        val messages = new ListBuffer[String]()

        Future {
           populateMessages(messages)
        }

        checkForMessages(messages)
    }


    def populateMessages(messages: ListBuffer[String]): ListBuffer[Serializable] = {
        Thread.sleep(1000)
        messages.append("message")
        populateMessages(messages)
    }

    def checkForMessages(messages: ListBuffer[String]) {
        Thread.sleep(3000)
        if (!messages.isEmpty) {
            println(s"Messages: ${messages.mkString(",")}")
            messages.clear()
        }
        checkForMessages(messages)
    }
}
