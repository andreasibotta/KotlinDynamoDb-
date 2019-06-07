package test.andreas.kotlindynamodb

import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.model.*
import java.util.*
import com.amazonaws.services.dynamodbv2.model.KeyType
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement
import java.util.ArrayList
import com.amazonaws.services.dynamodbv2.model.ProjectionType
import com.amazonaws.services.dynamodbv2.model.Projection
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput
import com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndex


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
                    AttributeDefinition("title", ScalarAttributeType.S),
                    AttributeDefinition("releaseDate", ScalarAttributeType.S),
                    AttributeDefinition("rating", ScalarAttributeType.N)

                )
                val provisionedThroughput = ProvisionedThroughput(10L, 10L)
//
                // PrecipIndex
                val releaseDateIndex = GlobalSecondaryIndex()
                    .withIndexName("ReleaseDateIndex")
                    .withProvisionedThroughput(provisionedThroughput)
                    .withProjection(Projection().withProjectionType(ProjectionType.ALL))

                val indexKeySchema = ArrayList<KeySchemaElement>()

                indexKeySchema.add(
                    KeySchemaElement()
                        .withAttributeName("releaseDate")
                        .withKeyType(KeyType.HASH)
                )  //Partition key
                indexKeySchema.add(
                    KeySchemaElement()
                        .withAttributeName("rating")
                        .withKeyType(KeyType.RANGE)
                )  //Sort key

                releaseDateIndex.setKeySchema(indexKeySchema)

                val table =
                    dynamoDB.createTable(
                        CreateTableRequest()
                            .withTableName(tableName)
                            .withKeySchema(keySchema)
                            .withAttributeDefinitions(attributeDefinitions)
                            .withProvisionedThroughput(provisionedThroughput)
                            .withGlobalSecondaryIndexes(releaseDateIndex)
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
