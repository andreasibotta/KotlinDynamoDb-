package test.andreas.kotlindynamodb

import com.amazonaws.services.dynamodbv2.datamodeling.*


@DynamoDBTable(tableName = "Issues")
class Issue {
    @get:DynamoDBHashKey(attributeName = "IssueId")
    @get:DynamoDBIndexRangeKey(attributeName = "IssueId", globalSecondaryIndexName = "CreateDateIndex")
    var issueId: String

    @get:DynamoDBRangeKey(attributeName = "Title")
    var title: String

    @get:DynamoDBAttribute
    var description: String

    @get:DynamoDBAttribute(attributeName = "CreateDate")
    @get:DynamoDBIndexHashKey(attributeName = "CreateDate", globalSecondaryIndexName = "CreateDateIndex")
    var createDate: String

    @get:DynamoDBAttribute
    var lastUpdateDate: String

    @get:DynamoDBAttribute(attributeName = "DueDate")
    var dueDate: String

    @get:DynamoDBAttribute
    var priority: Int = -1

    @get:DynamoDBAttribute
    var status: String

    constructor():
        this("","","","","","",1,"")

    constructor(issueId: String="", title: String="", description: String="", createDate: String="", lastUpdateDate: String="", dueDate: String="", priority: Int=-1, status: String="") {
        this.issueId = issueId
        this.title = title
        this.description = description
        this.createDate = createDate
        this.lastUpdateDate = lastUpdateDate
        this.dueDate = dueDate
        this.priority = priority
        this.status = status
    }

    override fun toString(): String {
        return "$issueId $title $description $createDate $lastUpdateDate $dueDate $priority $status"
    }
}
