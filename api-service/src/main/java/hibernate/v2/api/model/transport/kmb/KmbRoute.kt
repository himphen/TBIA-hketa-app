package hibernate.v2.api.model.transport.kmb

import com.google.gson.annotations.SerializedName
import hibernate.v2.api.model.transport.Bound

data class KmbRoute(
    @SerializedName("bound")
    val bound: Bound,
    @SerializedName("dest_en")
    val destEn: String,
    @SerializedName("dest_sc")
    val destSc: String,
    @SerializedName("dest_tc")
    val destTc: String,
    @SerializedName("orig_en")
    val origEn: String,
    @SerializedName("orig_sc")
    val origSc: String,
    @SerializedName("orig_tc")
    val origTc: String,
    @SerializedName("route")
    val routeId: String,
    @SerializedName("service_type")
    val serviceType: String,
)