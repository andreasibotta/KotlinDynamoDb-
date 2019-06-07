package test.andreas.kotlindynamodb

import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.model.*
import java.util.*

class TableCreator(private val tableName: String) {
    fun create() {
        val client = AmazonDynamoDBClientBuilder.standard()
            .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration("http://localhost:4569", "us-west-2"))
            .build()

        val dynamoDB = DynamoDB(client)

        val exists = dynamoDB.listTables().any {
            it.tableName == tableName
        }

        if (exists) {
            println("Table already exists")
        } else {
            try {
                println("Attempting to create table; please wait...")
                    val keySchema = Arrays.asList(
                        KeySchemaElement("title", KeyType.HASH), // Partition key
                        KeySchemaElement("year", KeyType.RANGE) //sort key
                    )
                val attributeDefinitions = Arrays.asList(
                        AttributeDefinition("year", ScalarAttributeType.N),
                        AttributeDefinition("title", ScalarAttributeType.S)
                    )
                val provisionedThroughput = ProvisionedThroughput(10L, 10L)
                val table =
                dynamoDB.createTable(
                    CreateTableRequest()
                        .withTableName(tableName)
                        .withKeySchema(keySchema)
                        .withAttributeDefinitions(attributeDefinitions)
                        .withProvisionedThroughput(provisionedThroughput)
                )

                table.waitForActive()
                println("Success.  Table status: " + table.description.tableStatus)

            } catch (e: Exception) {
                System.err.println("Unable to create table: ")
                System.err.println(e.message)
            }
        }
    }
}
