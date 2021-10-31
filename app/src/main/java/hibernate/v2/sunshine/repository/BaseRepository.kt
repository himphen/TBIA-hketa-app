package hibernate.v2.sunshine.repository

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

abstract class BaseRepository {
    val database by lazy { Firebase.database(FIREBASE_URL) }

    companion object {
        const val FIREBASE_URL = "https://android-tv-c733a-default-rtdb.asia-southeast1.firebasedatabase.app/"

        const val FIREBASE_REF_ROUTE = "public/route/"
        const val FIREBASE_REF_ROUTE_STOP = "public/routeStop/"
        const val FIREBASE_REF_STOP = "public/stop/"
        const val FIREBASE_REF_CHECKSUM = "config/checksum/"

        const val FIREBASE_DB_KMB = "kmb"
        const val FIREBASE_DB_NC = "citybusNwfb"
        const val FIREBASE_DB_NLB = "nlb"
        const val FIREBASE_DB_MTR = "mtr"
        const val FIREBASE_DB_LRT = "lrt"
        const val FIREBASE_DB_GMB = "gmb"
    }
}
