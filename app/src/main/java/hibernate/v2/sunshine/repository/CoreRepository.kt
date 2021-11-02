package hibernate.v2.sunshine.repository

import com.google.firebase.database.DatabaseException
import com.google.firebase.database.ktx.getValue
import hibernate.v2.api.model.transport.Checksum
import hibernate.v2.sunshine.util.getSnapshotValue

class CoreRepository : BaseRepository() {

    @Throws(DatabaseException::class)
    suspend fun getChecksum(): Checksum? {
        val routeRef = database.reference.child(FIREBASE_REF_CHECKSUM)
        val snapshot = routeRef.getSnapshotValue()
        return snapshot.getValue<Checksum>()
    }
}