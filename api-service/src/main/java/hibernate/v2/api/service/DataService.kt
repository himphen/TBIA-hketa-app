package hibernate.v2.api.service

import hibernate.v2.api.response.data.ChecksumResponse
import hibernate.v2.api.response.data.CtbDataResponse
import hibernate.v2.api.response.data.GmbDataResponse
import hibernate.v2.api.response.data.KmbDataResponse
import hibernate.v2.api.response.data.LrtDataResponse
import hibernate.v2.api.response.data.MtrDataResponse
import hibernate.v2.api.response.data.NlbDataResponse
import retrofit2.http.GET

interface DataService {
    @GET("checksum.json")
    suspend fun getChecksum(): ChecksumResponse

    @GET("kmb.json")
    suspend fun getKmbData(): KmbDataResponse

    @GET("ctb.json")
    suspend fun getCtbData(): CtbDataResponse

    @GET("gmb.json")
    suspend fun getGmbData(): GmbDataResponse

    @GET("lrt.json")
    suspend fun getLrtData(): LrtDataResponse

    @GET("mtr.json")
    suspend fun getMtrData(): MtrDataResponse

    @GET("nlb.json")
    suspend fun getNlbData(): NlbDataResponse
}
