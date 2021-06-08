package hibernate.v2.sunshine.db.eta

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

private const val DATABASE_NAME = "saved_eta"

/**
 * Database for storing all tracking data.
 */
@Database(
    entities = [EtaEntity::class, EtaOrderEntity::class],
    version = 1
)
@TypeConverters(EtaDataTypeConverter::class)
abstract class EtaDatabase : RoomDatabase() {
    abstract fun etaDao(): EtaDao
    abstract fun etaOrderDao(): EtaOrderDao

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL(
//                    "CREATE TABLE `Fruit` (`id` INTEGER, `name` TEXT, " +
//                            "PRIMARY KEY(`id`))"
//                )
            }
        }

        // For Singleton instantiation
        @Volatile
        private var INSTANCE: EtaDatabase? = null

        fun getInstance(context: Context): EtaDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): EtaDatabase {
            return Room.databaseBuilder(
                context,
                EtaDatabase::class.java,
                DATABASE_NAME
            )
                .allowMainThreadQueries() // TODO check what it is
                .build()
        }
    }
}
