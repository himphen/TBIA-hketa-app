package hibernate.v2.api.core

import hibernate.v2.utils.CommonLogger
import hibernate.v2.utils.isDebugBuild
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.resources.Resources
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object KtorClient {
    fun initClient(): HttpClient {
        return HttpClient(CIO) {
            expectSuccess = true

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
                deflate(1.0F)
                gzip(0.9F)
            }
        }
    }
}

class KtorLogger : Logger {
    override fun log(message: String) {
        CommonLogger.i(tag = "api") { message }
    }
}
