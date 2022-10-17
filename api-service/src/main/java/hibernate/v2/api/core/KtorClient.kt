package hibernate.v2.api.core

import io.ktor.client.plugins.logging.Logger

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
