package test.andreas.kotlindynamodb

import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap
import sun.security.krb5.internal.KDCOptions.with
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.document.*
import com.amazonaws.services.dynamodbv2.document.utils.NameMap
import com.amazonaws.services.dynamodbv2.model.Projection
import sun.security.pkcs11.wrapper.Functions.getAttributeName
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement
import com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndexDescription
import com.amazonaws.services.dynamodbv2.model.TableDescription


class MoviesReader {
    fun read() {
        val client = AmazonDynamoDBClientBuilder.standard()
            .withEndpointConfiguration(AwsClientBuilder.EndpointConfiguration("http://localhost:4569", "us-west-2"))
            .build()

        val mapper = DynamoDBMapper(client)

        val partitionKey = Movie()

        partitionKey.title = "Rush"
        val queryExpression = DynamoDBQueryExpression<Movie>()
            .withHashKeyValues(partitionKey)

        val itemList = mapper.query(Movie::class.java, queryExpression)

        for (movie in itemList) {
            val year = movie.year
            val title = movie.title
            val data = movie.directors
            val releaseData = movie.releaseDate
            println("Read movie: $year, $title, $data")
        }
    }

    fun readGSI() {
        val client = AmazonDynamoDBClientBuilder.standard().build()
        val dynamoDB = DynamoDB(client)

        val table = dynamoDB.getTable("Movies")
        val index = table.getIndex("ReleaseDateIndex")

        val spec = QuerySpec()
            .withKeyConditionExpression("#d = :v_date and rating = :v_precip")
            .withNameMap(
                NameMap()
                    .with("#d", "releaseDate")
            )
            .withValueMap(
                ValueMap()
                    .withString(":v_date", "2013-08-10")
                    .withNumber(":v_precip", 0)
            )

        val items = index.query(spec)
        val iter = items.iterator()
        while (iter.hasNext()) {
            println(iter.next().toJSONPretty())
        }
    }
//
//    fun describeGSIs() {
//        val client = AmazonDynamoDBClientBuilder.standard().build()
//        val dynamoDB = DynamoDB(client)
//
//        val table = dynamoDB.getTable("Movies")
//        val tableDesc = table.describe()
//
//
//        val gsiIter = tableDesc.globalSecondaryIndexes.iterator()
//        while (gsiIter.hasNext()) {
//            val gsiDesc = gsiIter.next()
//            println(
//                "Info for index "
//                        + gsiDesc.indexName + ":"
//            )
//
//            val kseIter = gsiDesc.keySchema.iterator()
//            while (kseIter.hasNext()) {
//                val kse = kseIter.next()
//                System.out.printf("\t%s: %s\n", kse.attributeName, kse.keyType)
//            }
//            val projection = gsiDesc.projection
//            println(("\tThe projection type is: " + projection.projectionType))
//            if (projection.projectionType.toString() == "INCLUDE") {
//                println(("\t\tThe non-key projected attributes are: " + projection.nonKeyAttributes))
//            }
//        }
//    }
}
