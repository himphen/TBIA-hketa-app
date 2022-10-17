package hibernate.v2.api.service

import hibernate.v2.api.BuildConfig
import hibernate.v2.api.core.KtorLogger
import hibernate.v2.api.request.eta.NlbRequest
import hibernate.v2.api.response.eta.EtaResponse
import hibernate.v2.api.response.eta.LrtEtaResponse
import hibernate.v2.api.response.eta.MTREtaResponse
import hibernate.v2.api.response.eta.NlbEtaResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.appendPathSegments
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object TransportService {
    private val client by lazy {
        HttpClient(CIO) {
            defaultRequest {
                url("https://rt.data.gov.hk/")
            }

            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                    }
                )
            }

            install(Logging) {
                logger = KtorLogger()
                level = if (BuildConfig.DEBUG) {
                    LogLevel.BODY
                } else {
                    LogLevel.NONE
                }
            }
        }
    }

    suspend fun getCtbStopEta(
        company: String,
        stopId: String,
        route: String
    ): EtaResponse {
        return client.get("v1/transport/citybus-nwfb/eta/") {
            url {
                appendPathSegments(company, stopId, route)
            }
        }.body()
    }

    suspend fun getNlbStopEta(
        body: NlbRequest
    ): NlbEtaResponse {
        return client.post("v1/transport/nlb/stop.php?action=estimatedArrivals") {
            setBody(body)
        }.body()
    }

    suspend fun getMtrStopEta(
        routeId: String,
        stopId: String,
    ): MTREtaResponse {
        return client.get("v1/transport/mtr/getSchedule.php") {
            url {
                parameter("line", routeId)
                parameter("sta", stopId)
            }
        }.body()
    }

    suspend fun getLrtStopEta(
        stopId: String,
    ): LrtEtaResponse {
        return client.get("v1/transport/mtr/lrt/getSchedule/") {
            url {
                parameter("station_id", stopId)
            }
        }.body()
    }
}
