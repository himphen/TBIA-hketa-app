package hibernate.v2.sunshine.core

import hibernate.v2.sunshine.db.LocalDatabase
import hibernate.v2.sunshine.repository.CoreRepository
import hibernate.v2.sunshine.repository.CtbRepository
import hibernate.v2.sunshine.repository.EtaRepository
import hibernate.v2.sunshine.repository.GmbRepository
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
    single { EtaRepository(get(), get()) }
    single { KmbRepository(get()) }
    single { CtbRepository(get()) }
    single { GmbRepository(get()) }
    single { MTRRepository(get()) }
    single { LRTRepository(get()) }
    single { NLBRepository(get()) }
}

val koinProviderModule: Module = module {
}
