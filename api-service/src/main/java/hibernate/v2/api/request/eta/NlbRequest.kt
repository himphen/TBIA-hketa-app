package hibernate.v2.api.request.eta

import hibernate.v2.api.request.BaseRequest

class NlbRequest(
    val routeId: String,
    val stopId: String,
    val language: String,
) : BaseRequest()
