package hibernate.v2.api.service

import hibernate.v2.api.response.eta.EtaResponse
import hibernate.v2.api.response.kmb.KmbRouteListResponse
import hibernate.v2.api.response.kmb.KmbRouteResponse
import hibernate.v2.api.response.kmb.KmbRouteStopListResponse
import hibernate.v2.api.response.kmb.KmbStopListResponse
import hibernate.v2.api.response.kmb.KmbStopResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface KmbService {
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

    @GET("v1/transport/kmb/route/{route}/{bound}/{service_type}")
    suspend fun getRoute(
        @Path("route") route: String,
        @Path("bound") bound: String,
        @Path("service_type") serviceType: String
    ): KmbRouteResponse

    @GET("v1/transport/kmb/route/")
    suspend fun getRouteList(): KmbRouteListResponse

    @GET("v1/transport/kmb/stop/")
    suspend fun getStopList(): KmbStopListResponse

    @GET("v1/transport/kmb/route-stop/")
    suspend fun getRouteStopList(): KmbRouteStopListResponse
}
