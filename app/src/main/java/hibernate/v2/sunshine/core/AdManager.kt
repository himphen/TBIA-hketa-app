package hibernate.v2.sunshine.core

class AdManager(val sharedPreferencesManager: SharedPreferencesManager) {

    fun shouldShowBannerAd(): Boolean {
        val value = sharedPreferencesManager.hideAdBannerUntil
        return if (value == 0L || System.currentTimeMillis() > value) {
            sharedPreferencesManager.hideAdBannerUntil = 0L
            true
        } else {
            false
        }
    }
}
