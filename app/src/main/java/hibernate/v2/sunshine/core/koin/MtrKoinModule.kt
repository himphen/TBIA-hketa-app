package hibernate.v2.sunshine.core.koin

import hibernate.v2.domain.mtr.GetRouteEtaCardList
import hibernate.v2.domain.mtr.GetRouteListDb
import hibernate.v2.domain.mtr.GetRouteListFromStopId
import hibernate.v2.domain.mtr.GetRouteStopComponentListDb
import hibernate.v2.domain.mtr.GetStopListDb
import hibernate.v2.domain.mtr.InitDatabase
import hibernate.v2.domain.mtr.MtrInteractor
import hibernate.v2.domain.mtr.SaveData
import hibernate.v2.domain.mtr.SetMapRouteListIntoMapStop
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
