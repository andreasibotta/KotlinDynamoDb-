package test.andreas.kotlindynamodb

import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.model.KeyType
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement
import java.util.ArrayList
import com.amazonaws.services.dynamodbv2.model.Projection
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput
import com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndex
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition


class TableCreator(private val tableName: String) {
    fun create() {

        val client = AmazonDynamoDBClientBuilder.standard()
            .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration("http://localhost:4569", "us-west-2"))
            .build()

        val dynamoDB = DynamoDB(client)

        // Attribute definitions
        val attributeDefinitions = ArrayList<AttributeDefinition>()

        attributeDefinitions.add(AttributeDefinition().withAttributeName("IssueId").withAttributeType("S"))
        attributeDefinitions.add(AttributeDefinition().withAttributeName("Title").withAttributeType("S"))
        attributeDefinitions.add(AttributeDefinition().withAttributeName("CreateDate").withAttributeType("S"))
        attributeDefinitions.add(AttributeDefinition().withAttributeName("DueDate").withAttributeType("S"))

        // Key schema for table
        val tableKeySchema = ArrayList<KeySchemaElement>()
        tableKeySchema.add(KeySchemaElement().withAttributeName("IssueId").withKeyType(KeyType.HASH)) // Partition
        // key
        tableKeySchema.add(KeySchemaElement().withAttributeName("Title").withKeyType(KeyType.RANGE)) // Sort
        // key

        // Initial provisioned throughput settings for the indexes
        val ptIndex = ProvisionedThroughput().withReadCapacityUnits(1L)
            .withWriteCapacityUnits(1L)

        // CreateDateIndex
        val createDateIndex = GlobalSecondaryIndex().withIndexName("CreateDateIndex")
            .withProvisionedThroughput(ptIndex)
            .withKeySchema(
                KeySchemaElement().withAttributeName("CreateDate").withKeyType(KeyType.HASH), // Partition
                // key
                KeySchemaElement().withAttributeName("IssueId").withKeyType(KeyType.RANGE)
            ) // Sort
            // key
            .withProjection(
                Projection().withProjectionType("INCLUDE").withNonKeyAttributes("Description", "Status")
            )

        // TitleIndex
        val titleIndex = GlobalSecondaryIndex().withIndexName("TitleIndex")
            .withProvisionedThroughput(ptIndex)
            .withKeySchema(
                KeySchemaElement().withAttributeName("Title").withKeyType(KeyType.HASH), // Partition
                // key
                KeySchemaElement().withAttributeName("IssueId").withKeyType(KeyType.RANGE)
            ) // Sort
            // key
            .withProjection(Projection().withProjectionType("KEYS_ONLY"))

        // DueDateIndex
        val dueDateIndex = GlobalSecondaryIndex().withIndexName("DueDateIndex")
            .withProvisionedThroughput(ptIndex)
            .withKeySchema(KeySchemaElement().withAttributeName("DueDate").withKeyType(KeyType.HASH)) // Partition
            // key
            .withProjection(Projection().withProjectionType("ALL"))

        val createTableRequest = CreateTableRequest().withTableName(tableName)
            .withProvisionedThroughput(
                ProvisionedThroughput().withReadCapacityUnits(1.toLong()).withWriteCapacityUnits(1.toLong())
            )
            .withAttributeDefinitions(attributeDefinitions).withKeySchema(tableKeySchema)
            .withGlobalSecondaryIndexes(createDateIndex, titleIndex, dueDateIndex)

        System.out.println("Creating table $tableName...")
        dynamoDB.createTable(createTableRequest)

        // Wait for table to become active
        System.out.println("Waiting for $tableName to become ACTIVE...")
        try {
            val table = dynamoDB.getTable(tableName)
            table.waitForActive()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

    }
}
