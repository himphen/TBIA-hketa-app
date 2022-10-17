package hibernate.v2.api.service

import hibernate.v2.api.BuildConfig
import hibernate.v2.api.core.KtorLogger
import hibernate.v2.api.response.eta.EtaResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.http.appendPathSegments
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object KmbService {
    private val client by lazy {
        HttpClient(CIO) {
            defaultRequest {
                url("https://data.etabus.gov.hk/")
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

    suspend fun getStopEta(
        stopId: String,
        route: String,
        serviceType: Int
    ): EtaResponse {
        return client.get("v1/transport/kmb/eta/") {
            url {
                appendPathSegments(stopId, route, serviceType.toString())
            }
        }.body()
    }
}
