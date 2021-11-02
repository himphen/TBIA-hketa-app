package hibernate.v2.sunshine.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
import hibernate.v2.sunshine.db.lrt.LRTDao
import hibernate.v2.sunshine.db.lrt.LRTRouteEntity
import hibernate.v2.sunshine.db.lrt.LRTRouteStopEntity
import hibernate.v2.sunshine.db.lrt.LRTStopEntity
import hibernate.v2.sunshine.db.mtr.MTRDao
import hibernate.v2.sunshine.db.mtr.MTRRouteEntity
import hibernate.v2.sunshine.db.mtr.MTRRouteStopEntity
import hibernate.v2.sunshine.db.mtr.MTRStopEntity
import hibernate.v2.sunshine.db.nc.NCDao
import hibernate.v2.sunshine.db.nc.NCRouteEntity
import hibernate.v2.sunshine.db.nc.NCRouteStopEntity
import hibernate.v2.sunshine.db.nc.NCStopEntity
import hibernate.v2.sunshine.db.nlb.NLBDao
import hibernate.v2.sunshine.db.nlb.NLBRouteEntity
import hibernate.v2.sunshine.db.nlb.NLBRouteStopEntity
import hibernate.v2.sunshine.db.nlb.NLBStopEntity
import java.util.concurrent.Executors

private const val DATABASE_NAME = "saved_data"

@Database(
    entities = [
        SavedEtaEntity::class, EtaOrderEntity::class,
        KmbRouteEntity::class, KmbStopEntity::class, KmbRouteStopEntity::class,
        NCRouteEntity::class, NCStopEntity::class, NCRouteStopEntity::class,
        GmbRouteEntity::class, GmbStopEntity::class, GmbRouteStopEntity::class,
        MTRRouteEntity::class, MTRStopEntity::class, MTRRouteStopEntity::class,
        LRTRouteEntity::class, LRTStopEntity::class, LRTRouteStopEntity::class,
        NLBRouteEntity::class, NLBStopEntity::class, NLBRouteStopEntity::class,
    ],
    version = 19,
    exportSchema = false
)
@TypeConverters(DataTypeConverter::class)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun etaDao(): EtaDao
    abstract fun etaOrderDao(): EtaOrderDao
    abstract fun kmbDao(): KmbDao
    abstract fun ncDao(): NCDao
    abstract fun gmbDao(): GmbDao
    abstract fun mtrDao(): MTRDao
    abstract fun lrtDao(): LRTDao
    abstract fun nlbDao(): NLBDao

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
