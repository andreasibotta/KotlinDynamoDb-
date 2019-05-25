package test.andreas.kotlindynamodb

import java.util.Arrays
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement
import com.amazonaws.services.dynamodbv2.model.KeyType
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType

fun main(args: Array<String>) {

    val client = AmazonDynamoDBClientBuilder.standard()
        .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration("http://localhost:4569", "us-west-2"))
        .build()

    val dynamoDB = DynamoDB(client)

    val tableName = "Movies"


    val exists = dynamoDB.listTables().any {
        it.tableName == tableName
    }
    println(exists)

    if (!exists) {
        try {
            println("Attempting to create table; please wait...")
            val table = dynamoDB.createTable(
                tableName,
                Arrays.asList(
                    KeySchemaElement("year", KeyType.HASH), // Partition
                    // key
                    KeySchemaElement("title", KeyType.RANGE)
                ), // Sort key
                Arrays.asList(
                    AttributeDefinition("year", ScalarAttributeType.N),
                    AttributeDefinition("title", ScalarAttributeType.S)
                ),
                ProvisionedThroughput(10L, 10L)
            )
            table.waitForActive()
            println("Success.  Table status: " + table.description.tableStatus)

        } catch (e: Exception) {
            System.err.println("Unable to create table: ")
            System.err.println(e.message)
        }
    }
}
