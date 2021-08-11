package hibernate.v2.api.service

import hibernate.v2.api.response.eta.LRTEtaResponse
import hibernate.v2.api.response.eta.MTREtaResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MTRService {
    @GET("v1/transport/mtr/getSchedule.php")
    suspend fun getMTRStopEta(
        @Query("line") routeId: String,
        @Query("sta") stopId: String,
    ): MTREtaResponse

    @GET("v1/transport/mtr/lrt/getSchedule")
    suspend fun getLRTStopEta(
        @Query("station_id") stopId: String,
    ): LRTEtaResponse
}
