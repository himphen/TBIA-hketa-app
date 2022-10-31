package hibernate.v2.sunshine.core.koin

import hibernate.v2.domain.eta.AddEta
import hibernate.v2.domain.eta.ClearAllEta
import hibernate.v2.domain.eta.ClearEta
import hibernate.v2.domain.eta.EtaInteractor
import hibernate.v2.domain.eta.GetCtbStopEtaApi
import hibernate.v2.domain.eta.GetEtaOrderList
import hibernate.v2.domain.eta.GetGmbStopEtaApi
import hibernate.v2.domain.eta.GetKmbStopEtaApi
import hibernate.v2.domain.eta.GetLrtStopEtaApi
import hibernate.v2.domain.eta.GetMTRStopEtaApi
import hibernate.v2.domain.eta.GetNlbStopEtaApi
import hibernate.v2.domain.eta.GetSavedGmbEtaList
import hibernate.v2.domain.eta.GetSavedKmbEtaList
import hibernate.v2.domain.eta.GetSavedLrtEtaList
import hibernate.v2.domain.eta.GetSavedMtrEtaList
import hibernate.v2.domain.eta.GetSavedNCEtaList
import hibernate.v2.domain.eta.GetSavedNlbEtaList
import hibernate.v2.domain.eta.HasEtaInDb
import hibernate.v2.domain.eta.UpdateEtaOrderList
import org.koin.core.module.Module
import org.koin.dsl.module

val koinEtaUseCaseModule: Module = module {

    single {
        EtaInteractor(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
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

    single { GetKmbStopEtaApi() }
    single { GetCtbStopEtaApi() }
    single { GetGmbStopEtaApi() }
    single { GetMTRStopEtaApi() }
    single { GetLrtStopEtaApi() }
    single { GetNlbStopEtaApi() }
    single { GetSavedKmbEtaList(get()) }
    single { GetSavedNCEtaList(get()) }
    single { GetSavedGmbEtaList(get()) }
    single { GetSavedMtrEtaList(get()) }
    single { GetSavedLrtEtaList(get()) }
    single { GetSavedNlbEtaList(get()) }
    single { HasEtaInDb(get()) }
    single { AddEta(get()) }
    single { ClearEta(get()) }
    single { ClearAllEta(get()) }
    single { GetEtaOrderList(get()) }
    single { UpdateEtaOrderList(get()) }
}
