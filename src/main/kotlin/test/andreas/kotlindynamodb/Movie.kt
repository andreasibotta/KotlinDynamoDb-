package test.andreas.kotlindynamodb

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable


@DynamoDBTable(tableName = "Movies")
class Movie() {
    @get:DynamoDBHashKey
    lateinit var year: Integer

    @get:DynamoDBRangeKey
    lateinit var title: String

    @get:DynamoDBAttribute
    var data: String = ""
}
