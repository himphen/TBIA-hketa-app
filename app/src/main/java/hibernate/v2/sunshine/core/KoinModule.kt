package hibernate.v2.sunshine.core

import hibernate.v2.sunshine.api.ApiManager
import hibernate.v2.sunshine.db.LocalDatabase
import hibernate.v2.sunshine.repository.CoreRepository
import hibernate.v2.sunshine.repository.EtaRepository
import hibernate.v2.sunshine.repository.GmbRepository
import hibernate.v2.sunshine.repository.KmbRepository
import hibernate.v2.sunshine.repository.LRTRepository
import hibernate.v2.sunshine.repository.MTRRepository
import hibernate.v2.sunshine.repository.NCRepository
import hibernate.v2.sunshine.repository.NLBRepository
import hibernate.v2.sunshine.repository.WeatherRepository
import hibernate.v2.sunshine.ui.eta.add.AddEtaMobileViewModel
import hibernate.v2.sunshine.ui.eta.add.AddEtaViewModel
import hibernate.v2.sunshine.ui.eta.edit.EditEtaViewModel
import hibernate.v2.sunshine.ui.eta.home.EtaViewModel
import hibernate.v2.sunshine.ui.main.mobile.MainViewModel
import hibernate.v2.sunshine.ui.onboarding.OnboardingViewModel
import hibernate.v2.sunshine.ui.route.mobile.RouteDetailsMobileViewModel
import hibernate.v2.sunshine.ui.searchmap.SearchMapViewModel
import hibernate.v2.sunshine.ui.traffic.TrafficViewModel
import hibernate.v2.sunshine.ui.weather.WeatherViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val koinServiceModule: Module = module {
    single { ApiManager(androidContext()) }
    single { SharedPreferencesManager(androidContext()) }
}

val koinUIModule: Module = module {
    viewModel { EtaViewModel(get()) }
    viewModel { OnboardingViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { EditEtaViewModel(get()) }
    viewModel { AddEtaViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { AddEtaMobileViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { WeatherViewModel(get()) }
    viewModel { TrafficViewModel(get()) }
    viewModel { SearchMapViewModel(get(), get(), get(), get(), get(), get()) }
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
    single { get<LocalDatabase>().ncDao() }
    single { get<LocalDatabase>().gmbDao() }
    single { get<LocalDatabase>().mtrDao() }
    single { get<LocalDatabase>().lrtDao() }
    single { get<LocalDatabase>().nlbDao() }
    single { CoreRepository() }
    single { EtaRepository(get(), get(), get()) }
    single { KmbRepository(get()) }
    single { NCRepository(get()) }
    single { GmbRepository(get()) }
    single { MTRRepository(get()) }
    single { LRTRepository(get()) }
    single { NLBRepository(get()) }
    single { WeatherRepository(get()) }
}
