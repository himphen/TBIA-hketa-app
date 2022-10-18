package hibernate.v2.api.service

import hibernate.v2.api.core.KtorClient
import hibernate.v2.api.response.eta.GmbEtaResponse
import io.ktor.client.call.body
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.resources.get
import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

object GmbService {
    private val client by lazy {
        KtorClient.initClient().config {
            defaultRequest {
                url("https://data.etagmb.gov.hk/")
            }
        }
    }

    suspend fun getStopEta(
        stopSeq: Int,
        route: String,
        serviceType: String
    ): GmbEtaResponse {
        return client.get(GetStopEta(route, serviceType, stopSeq)).body()
    }

    @Serializable
    @Resource("eta/route-stop/{route}/{serviceType}/{stopSeq}")
    class GetStopEta(
        val route: String,
        val serviceType: String,
        val stopSeq: Int,
    )
}
