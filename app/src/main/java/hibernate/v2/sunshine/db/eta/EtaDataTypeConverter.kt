package hibernate.v2.sunshine.db.eta

import androidx.room.TypeConverter
import java.util.UUID

class EtaDataTypeConverter {

    @TypeConverter
    fun fromUUID(uuid: UUID?): String? {
        return uuid?.toString()
    }

    @TypeConverter
    fun toUUID(uuid: String?): UUID? {
        return UUID.fromString(uuid)
    }
}
