package hibernate.v2.api.model.transport

import hibernate.v2.api.response.data.ChecksumDetailResponse
import hibernate.v2.api.response.data.ChecksumResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Checksum(
    var kmb: ChecksumDetail? = null,
    var ctb: ChecksumDetail? = null,
    var nlb: ChecksumDetail? = null,
    var mtr: ChecksumDetail? = null,
    var lrt: ChecksumDetail? = null,
    var gmb: ChecksumDetail? = null,
) {
    companion object {
        fun fromResponse(checksumResponse: ChecksumResponse?): Checksum {
            return Checksum(
                kmb = ChecksumDetail.fromResponse(checksumResponse?.kmb),
                ctb = ChecksumDetail.fromResponse(checksumResponse?.ctb),
                nlb = ChecksumDetail.fromResponse(checksumResponse?.nlb),
                mtr = ChecksumDetail.fromResponse(checksumResponse?.mtr),
                lrt = ChecksumDetail.fromResponse(checksumResponse?.lrt),
                gmb = ChecksumDetail.fromResponse(checksumResponse?.gmb),
            )
        }
    }

    fun isValid(): Boolean {
        return kmb != null && ctb != null && nlb != null && mtr != null && lrt != null && gmb != null
    }
}

@Serializable
data class ChecksumDetail(
    @SerialName("route")
    val route: String? = null,
    @SerialName("routeStop")
    val routeStop: String? = null,
    @SerialName("stop")
    val stop: String? = null
) {
    companion object {
        fun fromResponse(checksumDetailResponse: ChecksumDetailResponse?) =
            ChecksumDetail(
                route = checksumDetailResponse?.route,
                routeStop = checksumDetailResponse?.routeStop,
                stop = checksumDetailResponse?.stop
            )
    }
}
