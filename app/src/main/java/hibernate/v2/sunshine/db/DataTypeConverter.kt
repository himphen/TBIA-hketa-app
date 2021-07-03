package hibernate.v2.sunshine.db

import androidx.room.TypeConverter
import hibernate.v2.api.model.Bound
import hibernate.v2.sunshine.db.eta.Brand
import java.util.UUID

class DataTypeConverter {

    @TypeConverter
    fun fromUUID(uuid: UUID?): String? {
        return uuid?.toString()
    }

    @TypeConverter
    fun toUUID(uuid: String?): UUID? {
        return UUID.fromString(uuid)
    }

    @TypeConverter
    fun fromBound(value: Bound): String {
        return value.value
    }

    @TypeConverter
    fun toBound(value: String): Bound {
        return Bound.from(value)
    }

    @TypeConverter
    fun fromBrand(value: Brand): String {
        return value.value
    }

    @TypeConverter
    fun toBrand(value: String): Brand {
        return Brand.from(value)
    }
}
