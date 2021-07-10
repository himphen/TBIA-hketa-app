package hibernate.v2.sunshine.db

import androidx.room.TypeConverter
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.api.model.transport.GmbRegion
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
    fun fromCompany(value: Company): String {
        return value.value
    }

    @TypeConverter
    fun toCompany(value: String): Company {
        return Company.from(value)
    }

    @TypeConverter
    fun fromGmbRegion(value: GmbRegion): String {
        return value.value
    }

    @TypeConverter
    fun toGmbRegion(value: String): GmbRegion {
        return GmbRegion.from(value)
    }
}
