package hibernate.v2.database

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.AfterVersion
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.db.migrateWithCallbacks
import hibernate.v2.utils.CommonLogger

actual class DatabaseDriverFactory(private val context: Context) {

    actual var migrateCallbacks = hashMapOf<Int, () -> Unit>()

    actual var driver: SqlDriver? = null
    actual var database: MyDatabase? = null

    actual fun createDriver(): SqlDriver {
        if (driver == null) {
            driver = AndroidSqliteDriver(MyDatabase.Schema, context, "data.db")

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