package hibernate.v2.di

import hibernate.v2.domain.ctb.CtbInteractor
import hibernate.v2.domain.ctb.GetRouteListDb
import hibernate.v2.domain.ctb.GetRouteListFromStopId
import hibernate.v2.domain.ctb.GetRouteStopComponentListDb
import hibernate.v2.domain.ctb.GetStopListDb
import hibernate.v2.domain.ctb.InitDatabase
import hibernate.v2.domain.ctb.SaveData
import hibernate.v2.domain.ctb.SetMapRouteListIntoMapStop
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

    single { SaveData(get(), get()) }
    single { GetRouteListDb(get()) }
    single { GetRouteStopComponentListDb(get()) }
    single { InitDatabase(get()) }
    single { GetStopListDb(get()) }
    single { GetRouteListFromStopId(get()) }
    single { SetMapRouteListIntoMapStop() }
    single { hibernate.v2.domain.ctb.GetRouteEtaCardList(get()) }
}
