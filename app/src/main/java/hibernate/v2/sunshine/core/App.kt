package hibernate.v2.sunshine.core

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.himphen.logger.AndroidLogAdapter
import com.himphen.logger.Logger
import com.himphen.logger.PrettyFormatStrategy
import hibernate.v2.sunshine.api.ApiManager
import hibernate.v2.sunshine.db.LocalDatabase
import hibernate.v2.sunshine.repository.EtaRepository
import hibernate.v2.sunshine.repository.GmbRepository
import hibernate.v2.sunshine.repository.KmbRepository
import hibernate.v2.sunshine.repository.LRTRepository
import hibernate.v2.sunshine.repository.MTRRepository
import hibernate.v2.sunshine.repository.NCRepository
import hibernate.v2.sunshine.repository.WeatherRepository
import hibernate.v2.sunshine.ui.eta.EtaViewModel
import hibernate.v2.sunshine.ui.main.mobile.MainViewModel
import hibernate.v2.sunshine.ui.onboarding.OnboardingViewModel
import hibernate.v2.sunshine.ui.searchmap.SearchMapViewModel
import hibernate.v2.sunshine.ui.eta.edit.EditEtaViewModel
import hibernate.v2.sunshine.ui.eta.add.AddEtaViewModel
import hibernate.v2.sunshine.ui.traffic.TrafficViewModel
import hibernate.v2.sunshine.ui.weather.WeatherViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.logger.Level.ERROR
import org.koin.dsl.module

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // init logger
        val formatStrategy = PrettyFormatStrategy.newBuilder()
            .methodCount(3)
            .build()
        Logger.addLogAdapter(object : AndroidLogAdapter(formatStrategy) {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return hibernate.v2.sunshine.BuildConfig.DEBUG
            }
        })

        // dependency injection
        initKoin()
    }

    // dependency injection
    private val appModule = module {
        // singleton service
        single { ApiManager(androidContext()) }
        single { SharedPreferencesManager(androidContext()) }
        single { WeatherRepository(get()) }

        // database
        single { LocalDatabase.getInstance(androidContext()) }
        single { get<LocalDatabase>().etaDao() }
        single { get<LocalDatabase>().etaOrderDao() }
        single { get<LocalDatabase>().kmbDao() }
        single { get<LocalDatabase>().ncDao() }
        single { get<LocalDatabase>().gmbDao() }
        single { get<LocalDatabase>().mtrDao() }
        single { get<LocalDatabase>().lrtDao() }
        single { EtaRepository(get(), get(), get()) }
        single { KmbRepository(get(), get()) }
        single { NCRepository(get()) }
        single { GmbRepository(get()) }
        single { MTRRepository(get()) }
        single { LRTRepository(get()) }

        // ViewModels
        viewModel { EtaViewModel(get()) }
        viewModel { OnboardingViewModel(get(), get(), get(), get(), get()) }
        viewModel { EditEtaViewModel(get()) }
        viewModel { AddEtaViewModel(get(), get(), get(), get(), get(), get()) }
        viewModel { WeatherViewModel(get()) }
        viewModel { TrafficViewModel(get()) }
        viewModel { SearchMapViewModel(get(), get(), get(), get(), get()) }
        viewModel { MainViewModel() }
    }

    private fun initKoin() {
        startKoin {
            // https://github.com/InsertKoinIO/koin/issues/871#issuecomment-675231528
            androidLogger(ERROR)
            androidContext(this@App)
            modules(appModule)
        }
    }

}
