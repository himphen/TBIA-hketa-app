package hibernate.v2.sunshine.core.koin

import hibernate.v2.sunshine.domain.ctb.CtbInteractor
import hibernate.v2.sunshine.domain.ctb.GetRouteEtaCardList
import hibernate.v2.sunshine.domain.ctb.GetRouteListDb
import hibernate.v2.sunshine.domain.ctb.GetRouteListFromStopId
import hibernate.v2.sunshine.domain.ctb.GetRouteStopComponentListDb
import hibernate.v2.sunshine.domain.ctb.GetStopListDb
import hibernate.v2.sunshine.domain.ctb.InitDatabase
import hibernate.v2.sunshine.domain.ctb.SaveData
import hibernate.v2.sunshine.domain.ctb.SetMapRouteListIntoMapStop
import org.koin.core.module.Module
import org.koin.dsl.module

val koinCtbUseCaseModule: Module = module {
    single {
        CtbInteractor(
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
