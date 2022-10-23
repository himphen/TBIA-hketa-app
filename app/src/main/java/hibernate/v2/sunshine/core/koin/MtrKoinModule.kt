package hibernate.v2.sunshine.core.koin

import hibernate.v2.sunshine.domain.lrt.GetRouteEtaCardList
import hibernate.v2.sunshine.domain.lrt.GetRouteListDb
import hibernate.v2.sunshine.domain.lrt.GetRouteListFromStopId
import hibernate.v2.sunshine.domain.lrt.GetRouteStopComponentListDb
import hibernate.v2.sunshine.domain.lrt.GetStopListDb
import hibernate.v2.sunshine.domain.lrt.InitDatabase
import hibernate.v2.sunshine.domain.lrt.SaveData
import hibernate.v2.sunshine.domain.lrt.SetMapRouteListIntoMapStop
import hibernate.v2.sunshine.domain.mtr.MtrInteractor
import org.koin.core.module.Module
import org.koin.dsl.module

val koinMtrUseCaseModule: Module = module {
    single {
        MtrInteractor(
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
