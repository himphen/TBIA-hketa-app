package hibernate.v2.api.repository

import hibernate.v2.api.core.KtorClient
import hibernate.v2.api.response.data.ChecksumResponse
import hibernate.v2.api.response.data.CtbDataResponse
import hibernate.v2.api.response.data.GmbDataResponse
import hibernate.v2.api.response.data.KmbDataResponse
import hibernate.v2.api.response.data.LrtDataResponse
import hibernate.v2.api.response.data.MtrDataResponse
import hibernate.v2.api.response.data.NlbDataResponse
import hibernate.v2.utils.isDebugBuild
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get

class DataRepository(
    private val firebaseRemoteConfigRepository: FirebaseRemoteConfigRepository
) {
    private var _client: HttpClient? = null

    private suspend fun getClient(): HttpClient {
        return _client ?: run {
            var dataServiceBaseUrl: String = firebaseRemoteConfigRepository.apiBaseUrl()

            if (dataServiceBaseUrl.isEmpty()) {
                dataServiceBaseUrl = "https://localhost/"
            }

            _client = KtorClient.initClient().config {
                defaultRequest {
                    url(dataServiceBaseUrl)
                }

                install(Logging) {
                    level = if (isDebugBuild()) {
                        LogLevel.INFO
                    } else {
                        LogLevel.NONE
                    }
                }
            }

            _client!!
        }
    }

    suspend fun getChecksum(): ChecksumResponse {
        return getClient().get("checksum.json").body()
    }

    suspend fun getKmbData(): KmbDataResponse {
        return getClient().get("kmb.json").body()
    }

    suspend fun getCtbData(): CtbDataResponse {
        return getClient().get("ctb.json").body()
    }

    suspend fun getGmbData(): GmbDataResponse {
        return getClient().get("gmb.json").body()
    }

    suspend fun getLrtData(): LrtDataResponse {
        return getClient().get("lrt.json").body()
    }

    suspend fun getMtrData(): MtrDataResponse {
        return getClient().get("mtr.json").body()
    }

    suspend fun getNlbData(): NlbDataResponse {
        return getClient().get("nlb.json").body()
    }
}
