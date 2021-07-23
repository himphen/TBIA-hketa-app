package hibernate.v2.api.service

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Streaming

interface TrafficService {
    @Streaming
    @GET("traffic-snapshot-images/code/Traffic_Camera_Locations_Tc.csv")
    suspend fun getSnapshotCameraList(): Response<ResponseBody>
}