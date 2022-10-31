package hibernate.v2.domain.eta

import hibernate.v2.api.core.ApiSafeCall
import hibernate.v2.api.core.Resource
import hibernate.v2.api.model.transport.Company
import hibernate.v2.api.repository.TransportRepository
import hibernate.v2.api.response.eta.EtaResponse

class GetCtbStopEtaApi {
    suspend operator fun invoke(
        company: Company,
        stopId: String,
        route: String
    ): EtaResponse {
        val result = ApiSafeCall {
            TransportRepository.getCtbStopEta(
                company = company.value,
                stopId = stopId,
                route = route
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
