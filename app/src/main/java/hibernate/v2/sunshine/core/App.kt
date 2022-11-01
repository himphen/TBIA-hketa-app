package hibernate.v2.sunshine.core

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.himphen.logger.AndroidLogAdapter
import com.himphen.logger.Logger
import com.himphen.logger.PrettyFormatStrategy
import hibernate.v2.di.koinCtbUseCaseModule
import hibernate.v2.di.koinEtaUseCaseModule
import hibernate.v2.di.koinGmbUseCaseModule
import hibernate.v2.di.koinInteractorModule
import hibernate.v2.di.koinKmbUseCaseModule
import hibernate.v2.di.koinLrtUseCaseModule
import hibernate.v2.di.koinMtrUseCaseModule
import hibernate.v2.di.koinNlbUseCaseModule
import hibernate.v2.di.koinRepositoryModule
import hibernate.v2.di.koinUseCaseModule
import hibernate.v2.sunshine.BuildConfig
import hibernate.v2.sunshine.util.getAdMobDeviceID
import hibernate.v2.utils.initCommonLogger
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {

    override fun onCreate() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate()

        initLogger()
        initAdMob()
        initKoin()
    }

    // init logger
    private fun initLogger() {
        val formatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(false)
            .methodCount(2)
            .build()
        Logger.addLogAdapter(object : AndroidLogAdapter(formatStrategy) {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.DEBUG
            }
        })

        initCommonLogger()
    }

    private fun initKoin() {
        startKoin {
            androidLogger(level = if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@App)
            modules(
                koinAndroidServiceModule,
                koinAndroidRepositoryModule,
                koinAndroidUIModule,
                koinUseCaseModule,
                koinInteractorModule,
                koinRepositoryModule,
                koinKmbUseCaseModule,
                koinCtbUseCaseModule,
                koinGmbUseCaseModule,
                koinMtrUseCaseModule,
                koinLrtUseCaseModule,
                koinNlbUseCaseModule,
                koinEtaUseCaseModule
            )
        }
    }

    private fun initAdMob() {
        if (BuildConfig.DEBUG) {
            val testDevices = ArrayList<String>()
            testDevices.add(AdRequest.DEVICE_ID_EMULATOR)
            testDevices.add(getAdMobDeviceID(this))

            val requestConfiguration = RequestConfiguration.Builder()
                .setTestDeviceIds(testDevices)
                .build()
            MobileAds.setRequestConfiguration(requestConfiguration)
        }
    }
}
