package hibernate.v2.sunshine.core

import hibernate.v2.sunshine.db.LocalDatabase
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
import hibernate.v2.sunshine.domain.gmb.GetRouteEtaCardList
import hibernate.v2.sunshine.domain.gmb.GetRouteListDb
import hibernate.v2.sunshine.domain.gmb.GetRouteListFromStopId
import hibernate.v2.sunshine.domain.gmb.GetRouteStopComponentListDb
import hibernate.v2.sunshine.domain.gmb.GetStopListDb
import hibernate.v2.sunshine.domain.gmb.GmbInteractor
import hibernate.v2.sunshine.domain.gmb.InitDatabase
import hibernate.v2.sunshine.domain.gmb.SaveData
import hibernate.v2.sunshine.domain.gmb.SetMapRouteListIntoMapStop
import hibernate.v2.sunshine.repository.CoreRepository
import hibernate.v2.sunshine.repository.CtbRepository
import hibernate.v2.sunshine.repository.KmbRepository
import hibernate.v2.sunshine.repository.LRTRepository
import hibernate.v2.sunshine.repository.MTRRepository
import hibernate.v2.sunshine.repository.NLBRepository
import hibernate.v2.sunshine.ui.bookmark.BookmarkSaveViewModel
import hibernate.v2.sunshine.ui.bookmark.edit.BookmarkEditViewModel
import hibernate.v2.sunshine.ui.bookmark.home.BookmarkHomeViewModel
import hibernate.v2.sunshine.ui.main.mobile.MainViewModel
import hibernate.v2.sunshine.ui.onboarding.OnboardingViewModel
import hibernate.v2.sunshine.ui.route.details.mobile.RouteDetailsMobileViewModel
import hibernate.v2.sunshine.ui.route.list.leanback.RouteListLeanbackViewModel
import hibernate.v2.sunshine.ui.route.list.mobile.RouteListMobileViewModel
import hibernate.v2.sunshine.ui.searchmap.SearchMapViewModel
import hibernate.v2.sunshine.ui.traffic.TrafficViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val koinServiceModule: Module = module {
    single { SharedPreferencesManager(androidContext()) }
    single { AdManager(get()) }
}

val koinUIModule: Module = module {
    viewModel { BookmarkHomeViewModel(get()) }
    viewModel { OnboardingViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { BookmarkEditViewModel(get()) }
    viewModel { RouteListLeanbackViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { RouteListMobileViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { BookmarkSaveViewModel(get()) }
    viewModel { TrafficViewModel() }
    viewModel { SearchMapViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { MainViewModel() }
    viewModel { params ->
        RouteDetailsMobileViewModel(
            params.get(),
            params.get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
}

val koinRepositoryModule: Module = module {
    single { LocalDatabase.getInstance(androidContext()) }
    single { get<LocalDatabase>().etaDao() }
    single { get<LocalDatabase>().etaOrderDao() }
    single { get<LocalDatabase>().kmbDao() }
    single { get<LocalDatabase>().ctbDao() }
    single { get<LocalDatabase>().gmbDao() }
    single { get<LocalDatabase>().mtrDao() }
    single { get<LocalDatabase>().lrtDao() }
    single { get<LocalDatabase>().nlbDao() }
    single { CoreRepository() }
    single { KmbRepository(get()) }
    single { CtbRepository(get()) }
    single { MTRRepository(get()) }
    single { LRTRepository(get()) }
    single { NLBRepository(get()) }
}

val koinInteractorModule: Module = module {
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
}

val koinUseCaseModule: Module = module {
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
    single { SaveData(get()) }
    single { GetRouteListDb(get()) }
    single { GetRouteStopComponentListDb(get()) }
    single { InitDatabase(get()) }
    single { GetStopListDb(get()) }
    single { GetRouteListFromStopId(get()) }
    single { SetMapRouteListIntoMapStop() }
    single { GetRouteEtaCardList(get()) }
}
