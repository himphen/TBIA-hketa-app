package hibernate.v2.sunshine.repository

import hibernate.v2.api.core.ApiSafeCall
import hibernate.v2.api.core.Resource
import hibernate.v2.api.model.transport.Checksum
import hibernate.v2.api.service.DataService

class CoreRepository : BaseRepository() {

    suspend fun getChecksum(): Checksum {
        val result = ApiSafeCall { DataService.getChecksum() }

        val data = when (result) {
            is Resource.Success -> result.getData()
            is Resource.HttpError -> throw result.getThrowable()
            is Resource.OtherError -> throw result.getThrowable()
        }
        return Checksum.fromResponse(data)
    }
}
