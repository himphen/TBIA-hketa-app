package hibernate.v2.api.repository

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.remoteconfig.get
import dev.gitlive.firebase.remoteconfig.remoteConfig

object FirebaseRemoteConfigRepository : BaseRepository() {
    private val remoteConfig by lazy {
        Firebase.remoteConfig
    }

    val apiBaseUrl by lazy { remoteConfig.get<String>("api_base_url_v1") }
}
