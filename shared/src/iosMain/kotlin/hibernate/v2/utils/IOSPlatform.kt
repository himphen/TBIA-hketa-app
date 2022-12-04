package hibernate.v2.utils

import hibernate.v2.tbia.BuildKonfig
import platform.UIKit.UIDevice
import platform.darwin.NSObject

actual fun isDebugBuild(): Boolean {
    return true
}

abstract class AbstractIOSContext

class IOSContext : AbstractIOSContext()

actual typealias KMMContext = AbstractIOSContext

actual typealias KMMLocale = IOSLanguage

actual typealias KtorUnknownHostException = NSObject

class IOSLanguage(val code: Code = Code.ZH_TW) {
    enum class Code(val base: String, val moko: String) {
        DEFAULT("", ""),
        ZH_TW("zh_tw", "zh-tw"),
        EN("en", "en");

        companion object {
            fun from(type: String?) = values().find { it.base == type } ?: DEFAULT
        }

        fun toIOSLanguage(): IOSLanguage {
            return IOSLanguage(this)
        }
    }
}

fun reportEmailContent(): String {
    var text =
        "OS Version: " + UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion() + "\n"
    text += "Version: " + platform.Foundation.NSBundle.mainBundle.infoDictionary?.get("CFBundleShortVersionString") as? String + "\n"
    text += "Model: " + UIDevice.currentDevice.model + "\n\n\n"

    return text
}

actual fun gmsApiKey(): String {
    return BuildKonfig.GOOGLE_MAP_IOS_API_KEY
}