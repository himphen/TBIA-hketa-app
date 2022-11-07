package hibernate.v2.database

import com.squareup.sqldelight.db.AfterVersion
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.db.migrateWithCallbacks
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import hibernate.v2.utils.CommonLogger

actual class DatabaseDriverFactory {

    actual var migrateCallbacks = hashMapOf<Int, () -> Unit>()

    actual var driver: SqlDriver? = null
    actual var database: MyDatabase? = null

    actual fun createDriver(): SqlDriver {
        if (driver == null) {
            driver = NativeSqliteDriver(MyDatabase.Schema, "data.db")

            CommonLogger.d { "Database Current Version: ${MyDatabase.Schema.version}" }

            MyDatabase.Schema.migrateWithCallbacks(
                driver = driver!!,
                oldVersion = 0,
                newVersion = MyDatabase.Schema.version,
                *migrateCallbacks.entries.map { AfterVersion(it.key) { it.value() } }.toTypedArray()
            )
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