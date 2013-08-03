import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.client.IndicesAdminClient
import org.elasticsearch.common.io.stream.StreamOutput
import org.elasticsearch.node.NodeBuilder._
import collection.JavaConversions._

object TestElastic {

    def main(args: Array[String]) {
        val node = nodeBuilder().clusterName("homersimpson").client(true).node()
        val client = node.client()
        val health = client.admin.cluster.health(new ClusterHealthRequest()).get()
        println("Indices:")
        health.getIndices foreach(i => println(i._1))

        val result = client.search(new SearchRequest("jobs")).get
        println(s"total hits: ${result.getHits.totalHits()}")
    }

}
