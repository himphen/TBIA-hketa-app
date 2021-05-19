package hibernate.v2.api.service

import hibernate.v2.api.response.EtaResponse
import hibernate.v2.api.response.RouteResponse
import hibernate.v2.api.response.StopResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface EtaService {
    @GET("v1/transport/kmb/eta/{stop_id}/{route}/{service_type}")
    suspend fun getStopEta(
        @Path("stop_id") stopId: String,
        @Path("route") route: String,
        @Path("service_type") serviceType: Int
    ): EtaResponse

    @GET("v1/transport/kmb/stop/{stop_id}/")
    suspend fun getStop(
        @Path("stop_id") stopId: String
    ): StopResponse

    @GET("v1/transport/kmb/route/{route}/{bound}/{service_type}")
    suspend fun getRoute(
        @Path("route") route: String,
        @Path("bound") bound: String,
        @Path("service_type") serviceType: Int
    ): RouteResponse
}
