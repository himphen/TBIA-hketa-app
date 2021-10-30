package hibernate.v2.api.service

import hibernate.v2.api.response.eta.EtaResponse
import hibernate.v2.api.response.eta.LRTEtaResponse
import hibernate.v2.api.response.eta.MTREtaResponse
import hibernate.v2.api.response.nc.NCRouteListResponse
import hibernate.v2.api.response.nc.NCRouteResponse
import hibernate.v2.api.response.nc.NCRouteStopListResponse
import hibernate.v2.api.response.nc.NCStopResponse
import hibernate.v2.api.response.nlb.NLBRouteListResponse
import hibernate.v2.api.response.nlb.NLBRouteResponse
import hibernate.v2.api.response.nlb.NLBStopResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TransportService {

    // Citybus NWFB

    @GET("v1/transport/citybus-nwfb/eta/{company}/{stop_id}/{route}")
    suspend fun getNCStopEta(
        @Path("company") company: String,
        @Path("stop_id") stopId: String,
        @Path("route") route: String
    ): EtaResponse

    @GET("v1/transport/citybus-nwfb/route/{company}/{route}")
    suspend fun getNCRoute(
        @Path("company") company: String,
        @Path("route") route: String
    ): NCRouteResponse

    @GET("v1/transport/citybus-nwfb/route/{company}/")
    suspend fun getNCRouteList(
        @Path("company") company: String
    ): NCRouteListResponse

    @GET("v1/transport/citybus-nwfb/stop/{stop_id}")
    suspend fun getNCStop(
        @Path("stop_id") stopId: String
    ): NCStopResponse

    @GET("v1/transport/citybus-nwfb/route-stop/{company}/{route}/{bound}")
    suspend fun getNCRouteStop(
        @Path("company") company: String,
        @Path("route") route: String,
        @Path("bound") bound: String
    ): NCRouteStopListResponse

    @GET("v1/transport/mtr/getSchedule.php")
    suspend fun getMTRStopEta(
        @Query("line") routeId: String,
        @Query("sta") stopId: String,
    ): MTREtaResponse

    @GET("v1/transport/mtr/lrt/getSchedule")
    suspend fun getLRTStopEta(
        @Query("station_id") stopId: String,
    ): LRTEtaResponse

    // New Lantao Bus

    @GET("v1/transport/citybus-nwfb/eta/{company}/{stop_id}/{route}")
    suspend fun getNLBStopEta(
        @Path("company") company: String,
        @Path("stop_id") stopId: String,
        @Path("route") route: String
    ): EtaResponse

    @GET("v1/transport/nlb/route/{company}/{route}")
    suspend fun getNLBRoute(
        @Path("company") company: String,
        @Path("route") route: String
    ): NLBRouteResponse

    @GET("v1/transport/nlb/route.php?action=list")
    suspend fun getNLBRouteList(): NLBRouteListResponse

    @GET("v1/transport/nlb/stop/{stop_id}")
    suspend fun getNLBStop(
        @Path("stop_id") stopId: String
    ): NLBStopResponse
}
