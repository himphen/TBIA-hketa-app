package hibernate.v2.api.core

import hibernate.v2.utils.isDebugBuild
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.resources.Resources
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

actual object KtorClient {
    actual fun initClient(): HttpClient {
        return HttpClient(OkHttp) {
            expectSuccess = true

            engine {
                config {
                    followRedirects(true)
                }
            }

            install(Resources)
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        encodeDefaults = true
                        classDiscriminator = "discriminator"
                    }
                )
            }
            install(Logging) {
                logger = KtorLogger()
                level = if (isDebugBuild()) {
                    LogLevel.ALL
                } else {
                    LogLevel.NONE
                }
            }
            install(ContentEncoding) {
                gzip(0.9F)
                deflate(1.0F)
            }
        }
    }
}