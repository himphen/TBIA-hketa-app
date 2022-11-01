package hibernate.v2.database

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver

actual class DatabaseDriverFactory {
    actual var driver: SqlDriver? = null
    actual var database: MyDatabase? = null

    actual fun createDriver(): SqlDriver {
        if (driver == null) {
            driver = NativeSqliteDriver(MyDatabase.Schema, "data.db")
        }

        return driver as SqlDriver
    }

    actual fun createDatabase(): MyDatabase {
        if (database == null) {
            database = DatabaseFactory.createDatabase(driver!!)
        }

        return database as MyDatabase
    }
}