package hibernate.v2.sunshine.domain.eta

import hibernate.v2.api.core.ApiSafeCall
import hibernate.v2.api.core.Resource
import hibernate.v2.api.response.eta.EtaResponse
import hibernate.v2.api.service.KmbService

class GetKmbStopEtaApi {
    suspend operator fun invoke(
        stopId: String,
        route: String
    ): EtaResponse {
        val result = ApiSafeCall {
            KmbService.getStopEta(
                stopId = stopId,
                route = route,
                serviceType = 1
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
