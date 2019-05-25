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
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper



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


        val mapper = DynamoDBMapper(client)

        while (iter.hasNext()) {
            currentNode = iter.next() as ObjectNode

            val ayear = currentNode.path("year").asInt()
            val atitle = currentNode.path("title").asText()

            val image = currentNode.get("info").get("image_url").asText()

            val movie = Movie().apply {
                year = Integer(ayear)
                title = atitle
                data = image
            }
            try {
                mapper.save(movie)
                println("Wrote movie to database")
            } catch (e: Exception) {
                System.err.println("Unable to add movie: $ayear $atitle")
                System.err.println(e.message)
                break
            }

        }
        parser.close()
    }
}
