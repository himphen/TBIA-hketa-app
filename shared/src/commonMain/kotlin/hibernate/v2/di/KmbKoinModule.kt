package hibernate.v2.di

import hibernate.v2.domain.kmb.GetRouteEtaCardList
import hibernate.v2.domain.kmb.GetRouteListDb
import hibernate.v2.domain.kmb.GetRouteListFromStopId
import hibernate.v2.domain.kmb.GetRouteStopComponentListDb
import hibernate.v2.domain.kmb.GetStopListDb
import hibernate.v2.domain.kmb.InitDatabase
import hibernate.v2.domain.kmb.KmbInteractor
import hibernate.v2.domain.kmb.SaveData
import hibernate.v2.domain.kmb.SetMapRouteListIntoMapStop
import org.koin.core.module.Module
import org.koin.dsl.module

val koinKmbUseCaseModule: Module = module {
    single {
        KmbInteractor(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
        )
    }
    single { SaveData(get(), get()) }
    single { GetRouteListDb(get()) }
    single { GetRouteStopComponentListDb(get()) }
    single { InitDatabase(get()) }
    single { GetStopListDb(get()) }
    single { GetRouteListFromStopId(get()) }
    single { SetMapRouteListIntoMapStop() }
    single { GetRouteEtaCardList(get()) }
}
