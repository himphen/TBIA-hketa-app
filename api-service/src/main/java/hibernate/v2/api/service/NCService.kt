package hibernate.v2.api.service

import hibernate.v2.api.response.eta.EtaResponse
import hibernate.v2.api.response.nc.NCRouteListResponse
import hibernate.v2.api.response.nc.NCRouteResponse
import hibernate.v2.api.response.nc.NCRouteStopListResponse
import hibernate.v2.api.response.nc.NCStopResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface NCService {
    @GET("v1/transport/citybus-nwfb/eta/{company}/{stop_id}/{route}")
    suspend fun getStopEta(
        @Path("company") company: String,
        @Path("stop_id") stopId: String,
        @Path("route") route: String
    ): EtaResponse

    @GET("v1/transport/citybus-nwfb/route/{company}/{route}")
    suspend fun getRoute(
        @Path("company") company: String,
        @Path("route") route: String
    ): NCRouteResponse

    @GET("v1/transport/citybus-nwfb/route/{company}/")
    suspend fun getRouteList(
        @Path("company") company: String
    ): NCRouteListResponse

    @GET("v1/transport/citybus-nwfb/stop/{stop_id}")
    suspend fun getStop(
        @Path("stop_id") stopId: String
    ): NCStopResponse

    @GET("v1/transport/citybus-nwfb/route-stop/{company}/{route}/{bound}")
    suspend fun getRouteStop(
        @Path("company") company: String,
        @Path("route") route: String,
        @Path("bound") bound: String
    ): NCRouteStopListResponse
}
