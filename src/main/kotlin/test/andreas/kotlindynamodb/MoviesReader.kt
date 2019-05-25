package test.andreas.kotlindynamodb

import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression

class MoviesReader {
    fun read() {
        val client = AmazonDynamoDBClientBuilder.standard()
            .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration("http://localhost:4569", "us-west-2"))
            .build()

        val mapper = DynamoDBMapper(client)

        val partitionKey = Movie()

        partitionKey.year = Integer(2013)
        val queryExpression = DynamoDBQueryExpression<Movie>()
            .withHashKeyValues(partitionKey)

        val itemList = mapper.query(Movie::class.java, queryExpression)

        for (movie in itemList) {
            val year = movie.year
            val title = movie.title
            val data = movie.data
            println("Read movie: $year, $title, $data")
        }
    }
}
