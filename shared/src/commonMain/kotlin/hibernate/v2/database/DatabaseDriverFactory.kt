package hibernate.v2.database

import com.squareup.sqldelight.db.SqlDriver

expect class DatabaseDriverFactory {
    var driver: SqlDriver?

    fun createDriver(): SqlDriver
}