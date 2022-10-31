package hibernate.v2.domain.eta

import hibernate.v2.api.repository.TransportRepository
import hibernate.v2.api.request.eta.NlbRequest
import hibernate.v2.api.response.eta.NlbEtaResponse

class GetNlbStopEtaApi {
    suspend operator fun invoke(
        stopId: String,
        routeId: String
    ): NlbEtaResponse {
        val result = hibernate.v2.api.core.ApiSafeCall {
            TransportRepository.getNlbStopEta(
                NlbRequest(
                    routeId = routeId,
                    stopId = stopId,
                    language = "zh"
                )
            )
        }

        val data = when (result) {
            is hibernate.v2.api.core.Resource.Success -> result.getData()
            is hibernate.v2.api.core.Resource.HttpError -> throw result.getThrowable()
            is hibernate.v2.api.core.Resource.OtherError -> throw result.getThrowable()
        }

        return data
    }
}
