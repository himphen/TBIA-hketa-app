package hibernate.v2.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

fun initKoin() {
    val koinApp = startKoin {
        modules(
            koinUseCaseModule,
            koinInteractorModule,
            koinRepositoryModule,
            koinKmbUseCaseModule,
            koinCtbUseCaseModule,
            koinGmbUseCaseModule,
            koinMtrUseCaseModule,
            koinLrtUseCaseModule,
            koinNlbUseCaseModule,
            koinEtaUseCaseModule,
            koinIOSViewModelModule
        )
    }.koin
}

val koinIOSViewModelModule: Module = module {
}