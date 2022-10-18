package hibernate.v2.sunshine.domain.eta

import hibernate.v2.api.core.ApiSafeCall
import hibernate.v2.api.core.Resource
import hibernate.v2.api.response.eta.LrtEtaResponse
import hibernate.v2.api.service.TransportService

class GetLrtStopEtaApi {
    suspend operator fun invoke(
        stopId: String
    ): LrtEtaResponse {
        val result = ApiSafeCall {
            TransportService.getLrtStopEta(
                stopId = stopId
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
