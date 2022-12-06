package hibernate.v2.api.request.eta

import hibernate.v2.api.request.BaseRequest
import kotlinx.serialization.Serializable

@Serializable
class NlbRequest(
    val routeId: String,
    val stopId: String,
    val language: String,
) : BaseRequest()
