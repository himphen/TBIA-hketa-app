package hibernate.v2.sunshine.repository

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RemoteConfigRepository : BaseRepository() {

    val remoteConfig = Firebase.remoteConfig

    init {
        MainScope().launch {
            initRemoteConfig()
        }
    }

    private suspend fun initRemoteConfig() {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings).await()
        remoteConfig.setDefaultsAsync(mapOf("api_base_url_v1" to ""))
        remoteConfig.fetchAndActivate().await()
    }
}
