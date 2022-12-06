package hibernate.v2.tbia.core

import hibernate.v2.core.SharedPreferencesManager
import hibernate.v2.database.DatabaseDriverFactory
import hibernate.v2.tbia.ui.bookmark.BookmarkSaveViewModel
import hibernate.v2.tbia.ui.bookmark.edit.BookmarkEditViewModel
import hibernate.v2.tbia.ui.bookmark.home.BookmarkHomeViewModel
import hibernate.v2.tbia.ui.main.mobile.MainViewModel
import hibernate.v2.tbia.ui.onboarding.OnboardingViewModel
import hibernate.v2.tbia.ui.route.details.mobile.RouteDetailsMobileViewModel
import hibernate.v2.tbia.ui.route.list.leanback.RouteListLeanbackViewModel
import hibernate.v2.tbia.ui.route.list.mobile.RouteListMobileViewModel
import hibernate.v2.tbia.ui.searchmap.SearchMapViewModel
import hibernate.v2.tbia.ui.traffic.TrafficViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val koinAndroidServiceModule: Module = module {
    single { SharedPreferencesManager(androidApplication()) }
    single { AdManager(get()) }
}

val koinAndroidUIModule: Module = module {
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

val koinAndroidRepositoryModule: Module = module {
    single {
        val db = DatabaseDriverFactory(androidContext())
        db.migrateCallbacks = hashMapOf(
            2 to {
                val manager = get<SharedPreferencesManager>()
                manager.transportDataChecksum =
                    manager.transportDataChecksum?.apply {
                        nlb = null
                    }
            }
        )

        return@single db
    }
}

val koinInteractorModule: Module = module {
    single {
        hibernate.v2.domain.GeneralInteractor()
    }
}

val koinUseCaseModule: Module = module {
}
