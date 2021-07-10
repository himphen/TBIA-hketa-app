package hibernate.v2.api.service

import hibernate.v2.api.response.eta.EtaResponse
import hibernate.v2.api.response.gmb.GmbRouteResponse
import hibernate.v2.api.response.kmb.KmbStopResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface GmbService {
    @GET("v1/transport/kmb/eta/{stop_id}/{route}/{service_type}")
    suspend fun getStopEta(
        @Path("stop_id") stopId: String,
        @Path("route") route: String,
        @Path("service_type") serviceType: Int
    ): EtaResponse

    @GET("v1/transport/kmb/stop/{stop_id}/")
    suspend fun getStop(
        @Path("stop_id") stopId: String
    ): KmbStopResponse

    @GET("route/{region}/{route_no}")
    suspend fun getRoute(
        @Path("region") region: String,
        @Path("route_no") route: String
    ): GmbRouteResponse
}
