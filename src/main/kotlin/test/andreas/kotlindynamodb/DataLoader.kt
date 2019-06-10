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


class MoviesLoadData(private val tableName: String) {
    fun loadData(filename: String) {
        System.out.println("Loading data into table " + tableName + "...");

        // IssueId, Title,
        // Description,
        // CreateDate, LastUpdateDate, DueDate,
        // Priority, Status

        putItem(
            Issue(
                "A-101", "Compilation error", "Can't compile Project X - bad version number. What does this mean?",
                "2013-11-01", "2013-11-02", "2013-11-10", 1, "Assigned"
            )
        )

        putItem(
            Issue(
                "A-102", "Can't read data file", "The main data file is missing, or the permissions are incorrect",
                "2013-11-01", "2013-11-04", "2013-11-30", 2, "In progress"
            )
        )

        putItem(Issue("A-103", "Test failure", "Functional test of Project X produces errors", "2013-11-01", "2013-11-02",
            "2013-11-10", 1, "In progress"))

        putItem(Issue("A-104", "Compilation error", "Variable 'messageCount' was not initialized.", "2013-11-15",
            "2013-11-16", "2013-11-30", 3, "Assigned"))

        putItem(Issue("A-105", "Network issue", "Can't ping IP address 127.0.0.1. Please fix this.", "2013-11-15",
            "2013-11-16", "2013-11-19", 5, "Assigned"))
    }

    fun putItem(issue: Issue) {

        val client = AmazonDynamoDBClientBuilder.standard()
            .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration("http://localhost:4569", "us-west-2"))
            .build()

        val mapper = DynamoDBMapper(client)
        try {
            mapper.save(issue)
            println("Wrote movie to database")
        } catch (e: Exception) {
            System.err.println("Unable to add issue $issue")
            System.err.println(e.message)
        }
//        val item = Item().withPrimaryKey("IssueId", issueId).withString("Title", title)
//            .withString("Description", description).withString("CreateDate", createDate)
//            .withString("LastUpdateDate", lastUpdateDate).withString("DueDate", dueDate)
//            .withNumber("Priority", priority!!).withString("Status", status)

//        table.putItem(item)
    }

}
