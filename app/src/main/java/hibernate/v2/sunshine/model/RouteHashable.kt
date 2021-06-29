package hibernate.v2.sunshine.model

import androidx.room.PrimaryKey
import hibernate.v2.api.model.Bound
import java.util.UUID

interface RouteHashable {
    fun routeHashId(): String
}

interface StopHashable {
    fun stopHashId(): String
}