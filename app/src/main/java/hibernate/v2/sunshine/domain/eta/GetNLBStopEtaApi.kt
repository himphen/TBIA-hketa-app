package hibernate.v2.sunshine.domain.eta

import hibernate.v2.api.core.ApiSafeCall
import hibernate.v2.api.core.Resource
import hibernate.v2.api.request.eta.NlbRequest
import hibernate.v2.api.response.eta.NlbEtaResponse
import hibernate.v2.api.service.TransportService

class GetNLBStopEtaApi {
    suspend operator fun invoke(
        stopId: String,
        routeId: String
    ): NlbEtaResponse {
        val result = ApiSafeCall {
            TransportService.getNlbStopEta(
                NlbRequest(
                    routeId = routeId,
                    stopId = stopId,
                    language = "zh"
                )
            )
        }

        val data = when (result) {
            is Resource.Success -> result.getData()
            is Resource.HttpError -> throw result.getThrowable()
            is Resource.OtherError -> throw result.getThrowable()
        }

        return data
    }
}
