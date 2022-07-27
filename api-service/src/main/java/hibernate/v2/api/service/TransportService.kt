package hibernate.v2.api.service

import hibernate.v2.api.request.eta.NlbRequest
import hibernate.v2.api.response.eta.EtaResponse
import hibernate.v2.api.response.eta.LrtEtaResponse
import hibernate.v2.api.response.eta.MTREtaResponse
import hibernate.v2.api.response.eta.NlbEtaResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TransportService {
    @GET("v1/transport/citybus-nwfb/eta/{company}/{stop_id}/{route}")
    suspend fun getCtbStopEta(
        @Path("company") company: String,
        @Path("stop_id") stopId: String,
        @Path("route") route: String
    ): EtaResponse

    @POST("v1/transport/nlb/stop.php?action=estimatedArrivals")
    suspend fun getNlbStopEta(
        @Body body: NlbRequest
    ): NlbEtaResponse

    @GET("v1/transport/mtr/getSchedule.php")
    suspend fun getMtrStopEta(
        @Query("line") routeId: String,
        @Query("sta") stopId: String,
    ): MTREtaResponse

    @GET("v1/transport/mtr/lrt/getSchedule")
    suspend fun getLrtStopEta(
        @Query("station_id") stopId: String,
    ): LrtEtaResponse
}
