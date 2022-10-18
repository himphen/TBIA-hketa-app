package hibernate.v2.sunshine.core.koin

import hibernate.v2.sunshine.domain.lrt.GetRouteEtaCardList
import hibernate.v2.sunshine.domain.lrt.GetRouteListDb
import hibernate.v2.sunshine.domain.lrt.GetRouteListFromStopId
import hibernate.v2.sunshine.domain.lrt.GetRouteStopComponentListDb
import hibernate.v2.sunshine.domain.lrt.GetStopListDb
import hibernate.v2.sunshine.domain.lrt.InitDatabase
import hibernate.v2.sunshine.domain.lrt.LrtInteractor
import hibernate.v2.sunshine.domain.lrt.SaveData
import hibernate.v2.sunshine.domain.lrt.SetMapRouteListIntoMapStop
import org.koin.core.module.Module
import org.koin.dsl.module

val koinLrtUseCaseModule: Module = module {
    single {
        LrtInteractor(
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
