package hibernate.v2.api.service

import hibernate.v2.api.response.eta.MTREtaResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MTRService {
    @GET("https://rt.data.gov.hk/v1/transport/mtr/getSchedule.php")
    suspend fun getStopEta(
        @Query("line") routeId: String,
        @Query("sta") stopId: String,
    ): MTREtaResponse
}
