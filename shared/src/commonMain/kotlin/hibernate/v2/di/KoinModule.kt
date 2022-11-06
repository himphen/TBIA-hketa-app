package hibernate.v2.di

import hibernate.v2.api.repository.CoreRepository
import hibernate.v2.api.repository.DataRepository
import hibernate.v2.api.repository.FirebaseRemoteConfigRepository
import hibernate.v2.database.ctb.CtbDao
import hibernate.v2.database.eta.EtaDao
import hibernate.v2.database.eta.EtaOrderDao
import hibernate.v2.database.gmb.GmbDao
import hibernate.v2.database.kmb.KmbDao
import hibernate.v2.database.lrt.LrtDao
import hibernate.v2.database.mtr.MtrDao
import hibernate.v2.database.nlb.NlbDao
import hibernate.v2.domain.GeneralInteractor
import org.koin.core.module.Module
import org.koin.dsl.module

val koinInteractorModule: Module = module {
    single { GeneralInteractor() }
    single { DataRepository(get()) }
    single { FirebaseRemoteConfigRepository() }
}

val koinUseCaseModule: Module = module {
}

val koinRepositoryModule: Module = module {
    single { KmbDao(get()) }
    single { CtbDao(get()) }
    single { GmbDao(get()) }
    single { MtrDao(get()) }
    single { LrtDao(get()) }
    single { NlbDao(get()) }
    single { EtaDao(get()) }
    single { EtaOrderDao(get()) }
    single { CoreRepository(get()) }
}