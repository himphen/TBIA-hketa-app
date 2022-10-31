package hibernate.v2.sunshine.core.koin

import hibernate.v2.domain.gmb.GetRouteEtaCardList
import hibernate.v2.domain.gmb.GetRouteListDb
import hibernate.v2.domain.gmb.GetRouteListFromStopId
import hibernate.v2.domain.gmb.GetRouteStopComponentListDb
import hibernate.v2.domain.gmb.GetStopListDb
import hibernate.v2.domain.gmb.GmbInteractor
import hibernate.v2.domain.gmb.InitDatabase
import hibernate.v2.domain.gmb.SaveData
import hibernate.v2.domain.gmb.SetMapRouteListIntoMapStop
import org.koin.core.module.Module
import org.koin.dsl.module

val koinGmbUseCaseModule: Module = module {
    single {
        GmbInteractor(
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
