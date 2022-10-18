package hibernate.v2.api.core

import hibernate.v2.api.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
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
}

class KtorLogger : Logger {
    override fun log(message: String) {
        com.himphen.logger.Logger.log(
            com.himphen.logger.Logger.INFO,
            "api",
            message,
            null
        )
    }
}
