package hibernate.v2.api.repository

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.remoteconfig.FirebaseRemoteConfig
import dev.gitlive.firebase.remoteconfig.get
import dev.gitlive.firebase.remoteconfig.remoteConfig

class FirebaseRemoteConfigRepository : BaseRepository() {

    private var _remoteConfig: FirebaseRemoteConfig? = null

    private suspend fun getRemoteConfig(): FirebaseRemoteConfig {
        return _remoteConfig ?: run {
            _remoteConfig = Firebase.remoteConfig.apply {
                settings {
                    minimumFetchIntervalInSeconds = 3600
                }
                setDefaults(*defaultParams())
                fetchAndActivate()
            }

            _remoteConfig!!
        }
    }

    suspend fun apiBaseUrl() = getRemoteConfig().get<String>("api_base_url_v1")

    private fun defaultParams(): Array<Pair<String, String>> {
        return mapOf("api_base_url_v1" to "").map {
            Pair(it.key, it.value)
        }.toTypedArray()
    }
}
