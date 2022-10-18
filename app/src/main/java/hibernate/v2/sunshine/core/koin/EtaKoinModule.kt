package hibernate.v2.sunshine.core.koin

import hibernate.v2.sunshine.domain.eta.AddEta
import hibernate.v2.sunshine.domain.eta.ClearAllEta
import hibernate.v2.sunshine.domain.eta.ClearEta
import hibernate.v2.sunshine.domain.eta.EtaInteractor
import hibernate.v2.sunshine.domain.eta.GetCtbStopEtaApi
import hibernate.v2.sunshine.domain.eta.GetEtaOrderList
import hibernate.v2.sunshine.domain.eta.GetGmbStopEtaApi
import hibernate.v2.sunshine.domain.eta.GetKmbStopEtaApi
import hibernate.v2.sunshine.domain.eta.GetLRTStopEtaApi
import hibernate.v2.sunshine.domain.eta.GetMTRStopEtaApi
import hibernate.v2.sunshine.domain.eta.GetNLBStopEtaApi
import hibernate.v2.sunshine.domain.eta.GetSavedGmbEtaList
import hibernate.v2.sunshine.domain.eta.GetSavedKmbEtaList
import hibernate.v2.sunshine.domain.eta.GetSavedLRTEtaList
import hibernate.v2.sunshine.domain.eta.GetSavedMTREtaList
import hibernate.v2.sunshine.domain.eta.GetSavedNCEtaList
import hibernate.v2.sunshine.domain.eta.GetSavedNLBEtaList
import hibernate.v2.sunshine.domain.eta.HasEtaInDb
import hibernate.v2.sunshine.domain.eta.UpdateEtaOrderList
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
    single { GetLRTStopEtaApi() }
    single { GetNLBStopEtaApi() }
    single { GetSavedKmbEtaList(get()) }
    single { GetSavedNCEtaList(get()) }
    single { GetSavedGmbEtaList(get()) }
    single { GetSavedMTREtaList(get()) }
    single { GetSavedLRTEtaList(get()) }
    single { GetSavedNLBEtaList(get()) }
    single { HasEtaInDb(get()) }
    single { AddEta(get()) }
    single { ClearEta(get()) }
    single { ClearAllEta(get()) }
    single { GetEtaOrderList(get()) }
    single { UpdateEtaOrderList(get()) }
}
