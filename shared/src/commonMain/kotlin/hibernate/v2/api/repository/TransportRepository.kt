package hibernate.v2.api.repository

import hibernate.v2.api.core.KtorClient
import hibernate.v2.api.request.eta.NlbRequest
import hibernate.v2.api.response.eta.EtaResponse
import hibernate.v2.api.response.eta.LrtEtaResponse
import hibernate.v2.api.response.eta.MTREtaResponse
import hibernate.v2.api.response.eta.NlbEtaResponse
import io.ktor.client.call.body
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.resources.Resource
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object TransportRepository {
    private val client by lazy {
        KtorClient.initClient().config {
            defaultRequest {
                url("https://rt.data.gov.hk/")
            }
        }
    }

    suspend fun getCtbStopEta(
        company: String,
        stopId: String,
        route: String
    ): EtaResponse {
        return client.get(GetCtbStopEta(company, stopId, route)).body()
    }

    suspend fun getNlbStopEta(
        body: NlbRequest
    ): NlbEtaResponse {
        return client.post(GetNlbStopEta()) {
            contentType(ContentType.Application.Json)
            setBody(body)
        }.body()
    }

    suspend fun getMtrStopEta(
        routeId: String,
        stopId: String,
    ): MTREtaResponse {
        return client.get(GetMtrStopEta(routeId, stopId)).body()
    }

    suspend fun getLrtStopEta(
        stopId: String,
    ): LrtEtaResponse {
        return client.get(GetLrtStopEta(stopId)).body()
    }

    @Serializable
    @Resource("v2/transport/citybus/eta/{company}/{stopId}/{route}")
    class GetCtbStopEta(
        val company: String,
        val stopId: String,
        val route: String,
    )

    @Serializable
    @Resource("v1/transport/nlb/stop.php")
    class GetNlbStopEta(
        val action: String = "estimatedArrivals"
    )

    @Serializable
    @Resource("v1/transport/mtr/getSchedule.php")
    class GetMtrStopEta(
        val line: String,
        val sta: String,
    )

    @Serializable
    @Resource("v1/transport/mtr/lrt/getSchedule")
    class GetLrtStopEta(
        @SerialName("station_id")
        val stationId: String
    )
}
