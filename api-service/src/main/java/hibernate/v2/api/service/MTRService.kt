package hibernate.v2.api.service

import hibernate.v2.api.response.eta.MTREtaResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface MTRService {
    @GET("https://rt.data.gov.hk/v1/transport/mtr/getSchedule.php?line={line}&sta={sta}")
    suspend fun getStopEta(
        @Path("line") routeId: String,
        @Path("sta") stopId: String
    ): MTREtaResponse
}
