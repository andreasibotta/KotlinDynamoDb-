package test.andreas.kotlindynamodb

fun main() {
    val tableName = "Movies"
    TableCreator(tableName).create()
    MoviesLoadData().loadData(tableName, "moviedata.json")
}
