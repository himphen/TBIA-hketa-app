package hibernate.v2.di

import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
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
            koinEtaUseCaseModule
        )
    }
}