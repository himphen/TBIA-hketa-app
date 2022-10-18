package hibernate.v2.sunshine.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import hibernate.v2.sunshine.db.ctb.CtbDao
import hibernate.v2.sunshine.db.ctb.CtbRouteEntity
import hibernate.v2.sunshine.db.ctb.CtbRouteStopEntity
import hibernate.v2.sunshine.db.ctb.CtbStopEntity
import hibernate.v2.sunshine.db.eta.EtaDao
import hibernate.v2.sunshine.db.eta.EtaOrderDao
import hibernate.v2.sunshine.db.eta.EtaOrderEntity
import hibernate.v2.sunshine.db.eta.SavedEtaEntity
import hibernate.v2.sunshine.db.gmb.GmbDao
import hibernate.v2.sunshine.db.gmb.GmbRouteEntity
import hibernate.v2.sunshine.db.gmb.GmbRouteStopEntity
import hibernate.v2.sunshine.db.gmb.GmbStopEntity
import hibernate.v2.sunshine.db.kmb.KmbDao
import hibernate.v2.sunshine.db.kmb.KmbRouteEntity
import hibernate.v2.sunshine.db.kmb.KmbRouteStopEntity
import hibernate.v2.sunshine.db.kmb.KmbStopEntity
import hibernate.v2.sunshine.db.lrt.LrtDao
import hibernate.v2.sunshine.db.lrt.LrtRouteEntity
import hibernate.v2.sunshine.db.lrt.LrtRouteStopEntity
import hibernate.v2.sunshine.db.lrt.LrtStopEntity
import hibernate.v2.sunshine.db.mtr.MTRDao
import hibernate.v2.sunshine.db.mtr.MTRRouteEntity
import hibernate.v2.sunshine.db.mtr.MTRRouteStopEntity
import hibernate.v2.sunshine.db.mtr.MTRStopEntity
import hibernate.v2.sunshine.db.nlb.NlbDao
import hibernate.v2.sunshine.db.nlb.NlbRouteEntity
import hibernate.v2.sunshine.db.nlb.NlbRouteStopEntity
import hibernate.v2.sunshine.db.nlb.NlbStopEntity

private const val DATABASE_NAME = "saved_data"

@Database(
    entities = [
        SavedEtaEntity::class, EtaOrderEntity::class,
        KmbRouteEntity::class, KmbStopEntity::class, KmbRouteStopEntity::class,
        CtbRouteEntity::class, CtbStopEntity::class, CtbRouteStopEntity::class,
        GmbRouteEntity::class, GmbStopEntity::class, GmbRouteStopEntity::class,
        MTRRouteEntity::class, MTRStopEntity::class, MTRRouteStopEntity::class,
        LrtRouteEntity::class, LrtStopEntity::class, LrtRouteStopEntity::class,
        NlbRouteEntity::class, NlbStopEntity::class, NlbRouteStopEntity::class,
    ],
    version = 22,
    exportSchema = false
)
@TypeConverters(DataTypeConverter::class)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun etaDao(): EtaDao
    abstract fun etaOrderDao(): EtaOrderDao
    abstract fun kmbDao(): KmbDao
    abstract fun ctbDao(): CtbDao
    abstract fun gmbDao(): GmbDao
    abstract fun mtrDao(): MTRDao
    abstract fun lrtDao(): LrtDao
    abstract fun nlbDao(): NlbDao

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL(
//                    "CREATE TABLE `Fruit` (`id` INTEGER, `name` TEXT, " +
//                            "PRIMARY KEY(`id`))"
//                )
            }
        }

        fun getInstance(context: Context): LocalDatabase {
            val dbBuilder = Room.databaseBuilder(
                context,
                LocalDatabase::class.java,
                DATABASE_NAME
            )
            dbBuilder.fallbackToDestructiveMigration()
//            dbBuilder.setQueryCallback({ sqlQuery, bindArgs ->
//                Log.d("SQL", "Query: $sqlQuery SQL --- Args: $bindArgs")
//            }, Executors.newSingleThreadExecutor())
            return dbBuilder.build()
        }
    }
}
