package hibernate.v2.di

import hibernate.v2.core.SharedPreferencesManager
import hibernate.v2.database.DatabaseDriverFactory
import hibernate.v2.utils.IOSContext
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

fun initKoin() {
    val koinApp = startKoin {
        modules(
            koinIOSServiceModule,
            koinIOSRepositoryModule,
            koinIOSViewModelModule,
            koinServiceModule,
            koinInteractorModule,
            koinRepositoryModule,
            koinKmbUseCaseModule,
            koinCtbUseCaseModule,
            koinGmbUseCaseModule,
            koinMtrUseCaseModule,
            koinLrtUseCaseModule,
            koinNlbUseCaseModule,
            koinEtaUseCaseModule
        )
    }.koin
}

val koinIOSServiceModule: Module = module {
    single { SharedPreferencesManager(IOSContext()) }
}

val koinIOSViewModelModule: Module = module {
}

val koinIOSRepositoryModule: Module = module {
    single {
        val db = DatabaseDriverFactory()
        db.migrateCallbacks = hashMapOf(
            2 to {
                val manager = get<SharedPreferencesManager>()
                manager.transportDataChecksum =
                    manager.transportDataChecksum?.apply {
                        nlb = null
                    }
            }
        )

        return@single db
    }
}