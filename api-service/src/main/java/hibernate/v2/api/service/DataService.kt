package hibernate.v2.api.service

import android.util.Patterns
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import hibernate.v2.api.core.KtorClient
import hibernate.v2.api.response.data.ChecksumResponse
import hibernate.v2.api.response.data.CtbDataResponse
import hibernate.v2.api.response.data.GmbDataResponse
import hibernate.v2.api.response.data.KmbDataResponse
import hibernate.v2.api.response.data.LrtDataResponse
import hibernate.v2.api.response.data.MtrDataResponse
import hibernate.v2.api.response.data.NlbDataResponse
import io.ktor.client.call.body
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get

object DataService {
    private val client by lazy {
        var dataServiceBaseUrl = Firebase.remoteConfig.getString("api_base_url_v1")

        if (!Patterns.WEB_URL.matcher(dataServiceBaseUrl).matches()) {
            dataServiceBaseUrl = "https://localhost/"
        }

        KtorClient.initClient().config {
            defaultRequest {
                url(dataServiceBaseUrl)
            }
        }
    }

    suspend fun getChecksum(): ChecksumResponse {
        return client.get("checksum.json").body()
    }

    suspend fun getKmbData(): KmbDataResponse {
        return client.get("kmb.json").body()
    }

    suspend fun getCtbData(): CtbDataResponse {
        return client.get("ctb.json").body()
    }

    suspend fun getGmbData(): GmbDataResponse {
        return client.get("gmb.json").body()
    }

    suspend fun getLrtData(): LrtDataResponse {
        return client.get("lrt.json").body()
    }

    suspend fun getMtrData(): MtrDataResponse {
        return client.get("mtr.json").body()
    }

    suspend fun getNlbData(): NlbDataResponse {
        return client.get("nlb.json").body()
    }
}
