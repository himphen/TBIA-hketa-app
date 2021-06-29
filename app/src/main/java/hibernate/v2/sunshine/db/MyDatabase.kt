package hibernate.v2.sunshine.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import hibernate.v2.sunshine.db.eta.EtaDao
import hibernate.v2.sunshine.db.eta.EtaEntity
import hibernate.v2.sunshine.db.eta.EtaOrderDao
import hibernate.v2.sunshine.db.eta.EtaOrderEntity
import hibernate.v2.sunshine.db.kmb.KmbDao
import hibernate.v2.sunshine.db.kmb.KmbRouteEntity
import hibernate.v2.sunshine.db.kmb.KmbStopEntity

private const val DATABASE_NAME = "saved_data"

@Database(
    entities = [
        EtaEntity::class, EtaOrderEntity::class,
        KmbRouteEntity::class, KmbStopEntity::class
    ],
    version = 1
)
@TypeConverters(DataTypeConverter::class)
abstract class MyDatabase : RoomDatabase() {
    abstract fun etaDao(): EtaDao
    abstract fun etaOrderDao(): EtaOrderDao
    abstract fun kmbDao(): KmbDao

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL(
//                    "CREATE TABLE `Fruit` (`id` INTEGER, `name` TEXT, " +
//                            "PRIMARY KEY(`id`))"
//                )
            }
        }

        fun getInstance(context: Context): MyDatabase {
            return Room.databaseBuilder(
                context,
                MyDatabase::class.java,
                DATABASE_NAME
            )
                .allowMainThreadQueries() // TODO check what it is
                .build()
        }
    }
}
