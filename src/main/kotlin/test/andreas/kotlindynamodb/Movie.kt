package test.andreas.kotlindynamodb

import com.amazonaws.services.dynamodbv2.datamodeling.*


@DynamoDBTable(tableName = "Movies")
class Movie {
    @get:DynamoDBHashKey
    var year: Int = -1

    @get:DynamoDBRangeKey
    lateinit var title: String

    @get:DynamoDBAttribute
    lateinit var movieInfo: MovieInfo

    @DynamoDBDocument
    class MovieInfo {
        @get:DynamoDBAttribute
        lateinit var directors: Set<String>

        @get:DynamoDBAttribute
         var rating: Double = -1.0

        @get:DynamoDBAttribute
        lateinit var genres: Set<String>
    }
}
