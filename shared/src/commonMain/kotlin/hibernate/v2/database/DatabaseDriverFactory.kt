package hibernate.v2.database

import com.squareup.sqldelight.db.SqlDriver

expect class DatabaseDriverFactory {

    var migrateCallbacks: HashMap<Int, () -> Unit>

    var driver: SqlDriver?
    var database: MyDatabase?

    fun createDriver(): SqlDriver
    fun createDatabase(): MyDatabase
}