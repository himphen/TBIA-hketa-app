package hibernate.v2.sunshine.core

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import hibernate.v2.sunshine.api.ApiManager
import hibernate.v2.sunshine.api.DataRepository
import hibernate.v2.sunshine.ui.eta.EtaViewModel
import hibernate.v2.sunshine.ui.weather.WeatherViewModel
import hibernate.v2.sunshine.ui.traffic.TrafficViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.logger.Level.ERROR
import org.koin.dsl.module

class App : Application() {

    init {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

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
        single { DataRepository(get()) }
        // ViewModels
        viewModel { EtaViewModel(get()) }
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
