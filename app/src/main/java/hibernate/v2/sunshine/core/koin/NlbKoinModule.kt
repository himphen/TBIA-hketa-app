package hibernate.v2.sunshine.core.koin

import hibernate.v2.domain.nlb.GetRouteEtaCardList
import hibernate.v2.domain.nlb.GetRouteListDb
import hibernate.v2.domain.nlb.GetRouteListFromStopId
import hibernate.v2.domain.nlb.GetRouteStopComponentListDb
import hibernate.v2.domain.nlb.GetStopListDb
import hibernate.v2.domain.nlb.InitDatabase
import hibernate.v2.domain.nlb.NlbInteractor
import hibernate.v2.domain.nlb.SaveData
import hibernate.v2.domain.nlb.SetMapRouteListIntoMapStop
import org.koin.core.module.Module
import org.koin.dsl.module

val koinNlbUseCaseModule: Module = module {
    single {
        NlbInteractor(
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
