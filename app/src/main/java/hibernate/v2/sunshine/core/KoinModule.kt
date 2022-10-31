package hibernate.v2.sunshine.core

import hibernate.v2.database.DatabaseDriverFactory
import hibernate.v2.database.ctb.CtbDao
import hibernate.v2.database.eta.EtaDao
import hibernate.v2.database.eta.EtaOrderDao
import hibernate.v2.database.gmb.GmbDao
import hibernate.v2.database.kmb.KmbDao
import hibernate.v2.database.lrt.LrtDao
import hibernate.v2.database.mtr.MtrDao
import hibernate.v2.database.nlb.NlbDao
import hibernate.v2.domain.GeneralInteractor
import hibernate.v2.sunshine.repository.CoreRepository
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
    viewModel { SearchMapViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
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
    single { DatabaseDriverFactory(androidContext()) }
    single { KmbDao(get()) }
    single { CtbDao(get()) }
    single { GmbDao(get()) }
    single { MtrDao(get()) }
    single { LrtDao(get()) }
    single { NlbDao(get()) }
    single { EtaDao(get()) }
    single { EtaOrderDao(get()) }
    single { CoreRepository() }
}

val koinInteractorModule: Module = module {
    single {
        hibernate.v2.domain.GeneralInteractor()
    }
}

val koinUseCaseModule: Module = module {
}
