package hibernate.v2.sunshine.repository

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import hibernate.v2.sunshine.api.ApiManager
import hibernate.v2.sunshine.db.traffic.snapshot.CameraEntity
import hibernate.v2.sunshine.db.traffic.snapshot.SnapshotDao
import io.github.blackmo18.grass.dsl.grass
import okhttp3.ResponseBody
import retrofit2.Response

class TrafficRepository(
    private val apiManager: ApiManager,
    private val snapshotDao: SnapshotDao
) : BaseRepository() {

    suspend fun getSnapshotCameraListDb() = snapshotDao.getCameraList()

    private suspend fun getSnapshotCameraListApi(): Response<ResponseBody> {
        return apiManager.trafficService.getSnapshotCameraList()
    }

    suspend fun hasSnapshotCameraListDb() = snapshotDao.getSingleCamera() != null

    suspend fun saveCameraListApi() {
        getSnapshotCameraListApi().body()?.let { body ->
            val csvContents = csvReader {
                charset = "UTF-16"
                delimiter = '\t'
                skipEmptyLine = true
                skipMissMatchedRow = true
            }.readAllWithHeader(body.byteStream())

            val cameraList = grass<CameraEntity>().harvest(csvContents)
            snapshotDao.addCameraList(cameraList)
        } ?: run {
            // Error
        }
    }

    suspend fun clearCameraList() {
        snapshotDao.clearCameraList()
    }
}
