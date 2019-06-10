package test.andreas.kotlindynamodb

import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec
import com.amazonaws.services.dynamodbv2.document.*
import com.amazonaws.services.dynamodbv2.document.QueryOutcome
import com.amazonaws.services.dynamodbv2.document.ItemCollection


class MoviesReader(private val tableName: String) {
    fun read() {

        val client = AmazonDynamoDBClientBuilder.standard()
            .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration("http://localhost:4569", "us-west-2"))
            .build()

        val dynamoDB = DynamoDB(client)

        val table = dynamoDB.getTable(tableName)

        listOf("CreateDateIndex", "TitleIndex", "DueDateIndex").forEach { indexName ->
            println("\n***********************************************************\n")
            System.out.print("Querying index $indexName...")

            val index = table.getIndex(indexName)

            var items: ItemCollection<QueryOutcome>? = null

            val querySpec = QuerySpec()


            when {
                indexName === "CreateDateIndex" -> {
                    println("Issues filed on 2013-11-01")
                    querySpec.withKeyConditionExpression("CreateDate = :v_date and begins_with(IssueId, :v_issue)")
                        .withValueMap(ValueMap().withString(":v_date", "2013-11-01").withString(":v_issue", "A-"))
                    items = index.query(querySpec)
                }
                indexName === "TitleIndex" -> {
                    println("Compilation errors")
                    querySpec.withKeyConditionExpression("Title = :v_title and begins_with(IssueId, :v_issue)")
                        .withValueMap(
                            ValueMap().withString(":v_title", "Compilation error").withString(
                                ":v_issue",
                                "A-"
                            )
                        )
                    items = index.query(querySpec)
                }
                indexName === "DueDateIndex" -> {
                    println("Items that are due on 2013-11-30")
                    querySpec.withKeyConditionExpression("DueDate = :v_date")
                        .withValueMap(ValueMap().withString(":v_date", "2013-11-30"))
                    items = index.query(querySpec)
                }
                else -> {
                    println("\nNo valid index name provided")
                    return
                }
            }

            val iterator = items!!.iterator()

            println("Query: printing results...")

            while (iterator.hasNext()) {
                println(iterator.next().toJSONPretty())
            }
        }
    }
}
