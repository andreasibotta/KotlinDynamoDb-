package test.andreas.kotlindynamodb

import com.amazonaws.services.dynamodbv2.datamodeling.*


@DynamoDBTable(tableName = "Issues")
class Issue {
    @get:DynamoDBHashKey(attributeName = "IssueId")
    lateinit var issueId: String

    @get:DynamoDBRangeKey(attributeName = "Title")
    lateinit var title: String

    @get:DynamoDBAttribute
    lateinit var description: String

    @get:DynamoDBAttribute(attributeName = "CreateDate")
    lateinit  var createDate: String

    @get:DynamoDBAttribute
    lateinit var lastUpdateDate: String

    @get:DynamoDBAttribute(attributeName = "DueDate")
    lateinit var dueDate: String

    @get:DynamoDBAttribute
    var priority: Int = -1

    @get:DynamoDBAttribute
    lateinit var status: String

    constructor(issueId: String, title: String, description: String, createDate: String, lastUpdateDate: String, dueDate: String, priority: Int, status: String) {
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
