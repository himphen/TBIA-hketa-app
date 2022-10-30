package hibernate.v2.database

private const val DATABASE_NAME = "saved_data"

// @Database(
//    entities = [
//        SavedEtaEntity::class, SavedEtaOrderEntity::class,
//        KmbRouteEntity::class, KmbStopEntity::class, KmbRouteStopEntity::class,
//        CtbRouteEntity::class, CtbStopEntity::class, CtbRouteStopEntity::class,
//        GmbRouteEntity::class, GmbStopEntity::class, GmbRouteStopEntity::class,
//        MtrRouteEntity::class, MtrStopEntity::class, MtrRouteStopEntity::class,
//        LrtRouteEntity::class, LrtStopEntity::class, LrtRouteStopEntity::class,
//        NlbRouteEntity::class, NlbStopEntity::class, NlbRouteStopEntity::class,
//    ],
//    version = 22,
//    exportSchema = false
// )
// abstract class LocalDatabase : RoomDatabase() {
//    abstract fun etaDao(): EtaDao
//    abstract fun etaOrderDao(): EtaOrderDao
//    abstract fun kmbDao(): KmbDao
//    abstract fun ctbDao(): CtbDao
//    abstract fun gmbDao(): GmbDao
//    abstract fun mtrDao(): MtrDao
//    abstract fun lrtDao(): LrtDao
//    abstract fun nlbDao(): NlbDao
//
//    companion object {
//        private val MIGRATION_1_2 = object : Migration(1, 2) {
//            override fun migrate(database: SupportSQLiteDatabase) {
// //                database.execSQL(
// //                    "CREATE TABLE `Fruit` (`id` INTEGER, `name` TEXT, " +
// //                            "PRIMARY KEY(`id`))"
// //                )
//            }
//        }
//
//        fun getInstance(context: Context): LocalDatabase {
//            val dbBuilder = Room.databaseBuilder(
//                context,
//                LocalDatabase::class.java,
//                DATABASE_NAME
//            )
//            dbBuilder.fallbackToDestructiveMigration()
// //            dbBuilder.setQueryCallback({ sqlQuery, bindArgs ->
// //                Log.d("SQL", "Query: $sqlQuery SQL --- Args: $bindArgs")
// //            }, Executors.newSingleThreadExecutor())
//            return dbBuilder.build()
//        }
//    }
// }
