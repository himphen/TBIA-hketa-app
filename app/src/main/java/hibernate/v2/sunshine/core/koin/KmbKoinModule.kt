package hibernate.v2.sunshine.core.koin

import hibernate.v2.sunshine.domain.kmb.GetRouteEtaCardList
import hibernate.v2.sunshine.domain.kmb.GetRouteListDb
import hibernate.v2.sunshine.domain.kmb.GetRouteListFromStopId
import hibernate.v2.sunshine.domain.kmb.GetRouteStopComponentListDb
import hibernate.v2.sunshine.domain.kmb.GetStopListDb
import hibernate.v2.sunshine.domain.kmb.InitDatabase
import hibernate.v2.sunshine.domain.kmb.KmbInteractor
import hibernate.v2.sunshine.domain.kmb.SaveData
import hibernate.v2.sunshine.domain.kmb.SetMapRouteListIntoMapStop
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
    single { SaveData(get()) }
    single { GetRouteListDb(get()) }
    single { GetRouteStopComponentListDb(get()) }
    single { InitDatabase(get()) }
    single { GetStopListDb(get()) }
    single { GetRouteListFromStopId(get()) }
    single { SetMapRouteListIntoMapStop() }
    single { GetRouteEtaCardList(get()) }
}
