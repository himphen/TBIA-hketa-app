package hibernate.v2.api.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
open class BaseResponse(
    @SerializedName("generated_timestamp")
    val generatedTimestamp: String? = null,
    @SerializedName("type")
    val type: String? = null,
    @SerializedName("version")
    val version: String? = null
) : Parcelable