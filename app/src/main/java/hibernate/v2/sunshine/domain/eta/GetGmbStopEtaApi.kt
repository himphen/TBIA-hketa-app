package hibernate.v2.sunshine.domain.eta

import hibernate.v2.api.core.ApiSafeCall
import hibernate.v2.api.core.Resource
import hibernate.v2.api.repository.GmbRepository
import hibernate.v2.api.response.eta.GmbEtaResponse

class GetGmbStopEtaApi {
    suspend operator fun invoke(
        stopSeq: Int,
        serviceType: String,
        route: String
    ): GmbEtaResponse {
        val result = ApiSafeCall {
            GmbRepository.getStopEta(
                stopSeq = stopSeq,
                route = route,
                serviceType = serviceType
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
