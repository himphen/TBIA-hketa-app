package hibernate.v2.sunshine.db.eta

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

private const val DATABASE_NAME = "eta_data"

/**
 * Database for storing all tracking data.
 */
@Database(entities = [EtaEntity::class], version = 1)
@TypeConverters(EtaDataTypeConverter::class)
abstract class EtaDatabase : RoomDatabase() {
    abstract fun etaDao(): EtaDao

    companion object {
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
