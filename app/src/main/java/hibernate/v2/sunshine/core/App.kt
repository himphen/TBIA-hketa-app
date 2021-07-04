package hibernate.v2.sunshine.core

import android.app.Application
import com.himphen.logger.AndroidLogAdapter
import com.himphen.logger.Logger
import com.himphen.logger.PrettyFormatStrategy
import hibernate.v2.sunshine.api.ApiManager
import hibernate.v2.sunshine.api.WeatherRepository
import hibernate.v2.sunshine.db.MyDatabase
import hibernate.v2.sunshine.repository.EtaRepository
import hibernate.v2.sunshine.repository.KmbRepository
import hibernate.v2.sunshine.ui.eta.EtaViewModel
import hibernate.v2.sunshine.ui.onboarding.OnboardingViewModel
import hibernate.v2.sunshine.ui.settings.eta.SettingsEtaViewModel
import hibernate.v2.sunshine.ui.settings.eta.add.AddEtaViewModel
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
        single { WeatherRepository(get()) }
        // database
        single { MyDatabase.getInstance(androidContext()) }
        single { get<MyDatabase>().etaDao() }
        single { get<MyDatabase>().etaOrderDao() }
        single { get<MyDatabase>().kmbDao() }
        single { EtaRepository(get(), get(), get()) }
        single { KmbRepository(get(), get()) }

        // ViewModels
        viewModel { EtaViewModel(get()) }
        viewModel { OnboardingViewModel(get()) }
        viewModel { SettingsEtaViewModel(get(), get()) }
        viewModel { AddEtaViewModel(get()) }
        viewModel { WeatherViewModel(get()) }
        viewModel { TrafficViewModel(get()) }
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
