package hibernate.v2.api.repository

import hibernate.v2.api.core.ApiSafeCall
import hibernate.v2.api.core.Resource
import hibernate.v2.api.model.transport.Checksum

class CoreRepository(
    private val dataRepository: DataRepository,
) : BaseRepository() {

    suspend fun getChecksum(): Checksum {
        val result = ApiSafeCall { dataRepository.getChecksum() }

        val data = when (result) {
            is Resource.Success -> result.getData()
            is Resource.HttpError -> throw result.getThrowable()
            is Resource.OtherError -> throw result.getThrowable()
        }
        return Checksum.fromResponse(data)
    }
}
