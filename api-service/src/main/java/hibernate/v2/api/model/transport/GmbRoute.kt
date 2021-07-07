package hibernate.v2.api.model.transport

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

data class GmbRoute(
    @SerializedName("description_en")
    val descriptionEn: String,
    @SerializedName("description_sc")
    val descriptionSc: String,
    @SerializedName("description_tc")
    val descriptionTc: String,
    @SerializedName("directions")
    val directions: List<Direction>,
    @SerializedName("route_id")
    val routeId: Int
)

data class Direction(
    @SerializedName("dest_en")
    val destEn: String,
    @SerializedName("dest_sc")
    val destSc: String,
    @SerializedName("dest_tc")
    val destTc: String,
    @SerializedName("headways")
    val headways: List<Headway>,
    @SerializedName("orig_en")
    val origEn: String,
    @SerializedName("orig_sc")
    val origSc: String,
    @SerializedName("orig_tc")
    val origTc: String,
    @SerializedName("remarks_en")
    val remarksEn: String?,
    @SerializedName("remarks_sc")
    val remarksSc: String?,
    @SerializedName("remarks_tc")
    val remarksTc: String?,
    @SerializedName("route_seq")
    val routeSeq: Int
)

@Serializable
data class Headway(
    @SerializedName("end_time")
    val endTime: String?,
    @SerializedName("frequency")
    val frequency: Int?,
    @SerializedName("frequency_upper")
    val frequencyUpper: Int?,
    @SerializedName("headway_seq")
    val headwaySeq: Int,
    @SerializedName("public_holiday")
    val publicHoliday: Boolean,
    @SerializedName("start_time")
    val startTime: String,
    @SerializedName("weekdays")
    val weekdays: List<Boolean>
)