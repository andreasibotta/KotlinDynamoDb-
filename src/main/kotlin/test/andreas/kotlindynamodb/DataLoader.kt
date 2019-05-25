package test.andreas.kotlindynamodb

import java.io.File

import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.document.Item
import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode

class MoviesLoadData {
    fun loadData(tableName: String, filename: String) {
        val client = AmazonDynamoDBClientBuilder.standard()
            .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration("http://localhost:4569", "us-west-2"))
            .build()

        val dynamoDB = DynamoDB(client)

        val table = dynamoDB.getTable(tableName)

        val parser = JsonFactory().createParser(File(filename))

        val rootNode = ObjectMapper().readTree<JsonNode>(parser)
        val iter = rootNode.iterator()

        var currentNode: ObjectNode

        while (iter.hasNext()) {
            currentNode = iter.next() as ObjectNode

            val year = currentNode.path("year").asInt()
            val title = currentNode.path("title").asText()

            try {
                table.putItem(
                    Item().withPrimaryKey("year", year, "title", title).withJSON(
                        "info",
                        currentNode.path("info").toString()
                    )
                )
                println("PutItem succeeded: $year $title")

            } catch (e: Exception) {
                System.err.println("Unable to add movie: $year $title")
                System.err.println(e.message)
                break
            }

        }
        parser.close()
    }
}
