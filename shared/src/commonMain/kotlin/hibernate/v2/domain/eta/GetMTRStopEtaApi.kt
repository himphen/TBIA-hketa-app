package hibernate.v2.domain.eta

import hibernate.v2.api.core.ApiSafeCall
import hibernate.v2.api.core.Resource
import hibernate.v2.api.repository.TransportRepository
import hibernate.v2.api.response.eta.MTREtaResponse

class GetMTRStopEtaApi {
    suspend operator fun invoke(
        stopId: String,
        route: String
    ): MTREtaResponse {
        val result = ApiSafeCall {
            TransportRepository.getMtrStopEta(
                stopId = stopId,
                routeId = route
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
