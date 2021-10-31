package hibernate.v2.api.service

import hibernate.v2.api.response.eta.EtaResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface KmbService {
    @GET("v1/transport/kmb/eta/{stop_id}/{route}/{service_type}")
    suspend fun getStopEta(
        @Path("stop_id") stopId: String,
        @Path("route") route: String,
        @Path("service_type") serviceType: Int
    ): EtaResponse
}
