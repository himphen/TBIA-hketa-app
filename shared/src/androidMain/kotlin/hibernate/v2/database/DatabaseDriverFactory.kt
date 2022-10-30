package hibernate.v2.database

import android.content.Context
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver

actual class DatabaseDriverFactory(private val context: Context) {
    actual var driver: SqlDriver? = null

    actual fun createDriver(): SqlDriver {
        if (driver == null) {
            driver = AndroidSqliteDriver(MyDatabase.Schema, context, "data.db")
        }

        return driver as SqlDriver
    }
}