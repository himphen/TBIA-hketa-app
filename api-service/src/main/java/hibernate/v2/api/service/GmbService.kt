package hibernate.v2.api.service

import hibernate.v2.api.response.eta.GmbEtaResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface GmbService {
    @GET("eta/route-stop/{route_id}/{service_type}/{stop_seq}")
    suspend fun getStopEta(
        @Path("stop_seq") stopSeq: Int,
        @Path("route_id") route: String,
        @Path("service_type") serviceType: String
    ): GmbEtaResponse
}
