package hibernate.v2.api.repository

import hibernate.v2.api.core.KtorClient
import hibernate.v2.api.response.eta.EtaResponse
import io.ktor.client.call.body
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.resources.get
import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

object KmbRepository {
    private val client by lazy {
        KtorClient.initClient().config {
            defaultRequest {
                url("https://data.etabus.gov.hk/")
            }
        }
    }

    suspend fun getStopEta(
        stopId: String,
        route: String,
        serviceType: Int
    ): EtaResponse {
        return client.get(GetStopEta(stopId, route, serviceType)).body()
    }

    @Serializable
    @Resource("v1/transport/kmb/eta/{stopId}/{route}/{serviceType}")
    class GetStopEta(
        val stopId: String,
        val route: String,
        val serviceType: Int
    )
}
