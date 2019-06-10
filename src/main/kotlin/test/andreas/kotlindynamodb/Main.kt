package test.andreas.kotlindynamodb

fun main() {
    val tableName = "Issues"
    TableCreator(tableName).create()
    IssuesLoadData(tableName).loadData("smallMovies.json")
//
    IssuesReader(tableName).read()
//
//    IssuesReader().readGSI()
}

