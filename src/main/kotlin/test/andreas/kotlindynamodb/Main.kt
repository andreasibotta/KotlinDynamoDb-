package test.andreas.kotlindynamodb

fun main() {
    val tableName = "Movies"
    TableCreator(tableName).create()
    MoviesLoadData(tableName).loadData("smallMovies.json")
//
    MoviesReader(tableName).read()
//
//    MoviesReader().readGSI()
}

