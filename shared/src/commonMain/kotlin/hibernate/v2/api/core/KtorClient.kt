package hibernate.v2.api.core

import hibernate.v2.utils.CommonLogger
import io.ktor.client.HttpClient
import io.ktor.client.plugins.logging.Logger

expect object KtorClient {
    fun initClient(): HttpClient
}

class KtorLogger : Logger {
    override fun log(message: String) {
        CommonLogger.i(tag = "api") { message }
    }
}
