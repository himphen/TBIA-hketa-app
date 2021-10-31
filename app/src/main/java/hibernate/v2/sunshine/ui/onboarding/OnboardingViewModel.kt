package hibernate.v2.sunshine.ui.onboarding

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.himphen.logger.Logger
import hibernate.v2.sunshine.core.SharedPreferencesManager
import hibernate.v2.sunshine.repository.CoreRepository
import hibernate.v2.sunshine.repository.GmbRepository
import hibernate.v2.sunshine.repository.KmbRepository
import hibernate.v2.sunshine.repository.LRTRepository
import hibernate.v2.sunshine.repository.MTRRepository
import hibernate.v2.sunshine.repository.NCRepository
import hibernate.v2.sunshine.repository.NLBRepository
import hibernate.v2.sunshine.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val sharedPreferencesManager: SharedPreferencesManager,
    private val coreRepository: CoreRepository,
    private val kmbRepository: KmbRepository,
    private val ncRepository: NCRepository,
    private val gmbRepository: GmbRepository,
    private val mtrRepository: MTRRepository,
    private val lrtRepository: LRTRepository,
    private val nlbRepository: NLBRepository,
) : BaseViewModel() {

    val fetchTransportDataRequired = MutableLiveData<Boolean>()
    val fetchTransportDataCompleted = MutableLiveData<FetchTransportDataType>()
    val fetchTransportDataFailed = MutableLiveData<FetchTransportDataType>()

    fun checkDbTransportData() {
        viewModelScope.launch(Dispatchers.IO) {

            val checksum = coreRepository.getChecksum()
            val existingChecksum = sharedPreferencesManager.transportDataChecksum

            val updateCheck = UpdateCheck()

            if (checksum == null || existingChecksum == null) {
                sharedPreferencesManager.transportDataChecksum = checksum
            } else {
                if (checksum == existingChecksum) {
                    fetchTransportDataRequired.postValue(false)
                    return@launch
                }

                updateCheck.kmb = checksum.kmb != existingChecksum.kmb
                updateCheck.nc = checksum.citybusNwfb != existingChecksum.citybusNwfb
                updateCheck.gmb = checksum.gmb != existingChecksum.gmb
                updateCheck.mtr = checksum.mtr != existingChecksum.mtr
                updateCheck.lrt = checksum.lrt != existingChecksum.lrt
                updateCheck.nlb = checksum.nlb != existingChecksum.nlb
            }

            fetchTransportDataRequired.postValue(true)

            if (updateCheck.kmb) {
                try {
                    downloadKmbTransportData()
                    fetchTransportDataCompleted.postValue(FetchTransportDataType.KMB)
                } catch (e: Exception) {
                    Logger.e(e, "=== lifecycle downloadKmbTransportData error ===")
                    fetchTransportDataFailed.postValue(FetchTransportDataType.KMB)
                    return@launch
                }
            }

            if (updateCheck.nc) {
                try {
                    downloadNCTransportData()
                    fetchTransportDataCompleted.postValue(FetchTransportDataType.NC)
                } catch (e: Exception) {
                    Logger.e(e, "=== lifecycle downloadNCTransportData error ===")
                    fetchTransportDataFailed.postValue(FetchTransportDataType.NC)
                    return@launch
                }
            }

            if (updateCheck.gmb) {
                try {
                    downloadGmbTransportData()
                    fetchTransportDataCompleted.postValue(FetchTransportDataType.GMB)
                } catch (e: Exception) {
                    Logger.e(e, "=== lifecycle downloadGmbTransportData error ===")
                    fetchTransportDataFailed.postValue(FetchTransportDataType.GMB)
                    return@launch
                }
            }

            if (updateCheck.mtr) {
                try {
                    downloadMTRTransportData()
                    fetchTransportDataCompleted.postValue(FetchTransportDataType.MTR)
                } catch (e: Exception) {
                    Logger.e(e, "=== lifecycle downloadMTRTransportData error ===")
                    fetchTransportDataFailed.postValue(FetchTransportDataType.MTR)
                    return@launch
                }
            }

            if (updateCheck.lrt) {
                try {
                    downloadLRTTransportData()
                    fetchTransportDataCompleted.postValue(FetchTransportDataType.LRT)
                } catch (e: Exception) {
                    Logger.e(e, "=== lifecycle downloadLRTTransportData error ===")
                    fetchTransportDataFailed.postValue(FetchTransportDataType.LRT)
                    return@launch
                }
            }

            if (updateCheck.nlb) {
                try {
                    downloadNLBTransportData()
                    fetchTransportDataCompleted.postValue(FetchTransportDataType.NLB)
                } catch (e: Exception) {
                    Logger.e(e, "=== lifecycle downloadNLBTransportData error ===")
                    fetchTransportDataFailed.postValue(FetchTransportDataType.NLB)
                    return@launch
                }
            }

            fetchTransportDataCompleted.postValue(FetchTransportDataType.ALL)
        }
    }

    private suspend fun downloadKmbTransportData() {
        Logger.d("=== lifecycle downloadKmbTransportData start ===")
        kmbRepository.initDatabase()

        coroutineScope {
            listOf(
                launch {
                    kmbRepository.saveStopListFromFirebase()
                    Logger.d("lifecycle downloadKmbTransportData saveStopListApi done")
                },
                launch {
                    kmbRepository.saveRouteListFromFirebase()
                    Logger.d("lifecycle downloadKmbTransportData saveRouteListApi done")
                },
                launch {
                    kmbRepository.saveRouteStopListFromFirebase()
                    Logger.d("lifecycle downloadKmbTransportData saveRouteStopListApi done")
                }
            ).joinAll()
        }
    }

    private suspend fun downloadNCTransportData() {
        Logger.d("=== lifecycle downloadNCTransportData start ===")
        ncRepository.initDatabase()

        coroutineScope {
            listOf(
                launch {
                    ncRepository.saveRouteListFromFirebase()
                    Logger.d("lifecycle downloadNCTransportData saveRouteListFromFirebase done")
                },
                launch {
                    ncRepository.saveRouteStopListFromFirebase()
                    Logger.d("lifecycle downloadNCTransportData saveRouteStopListFromFirebase done")
                },
                launch {
                    ncRepository.saveStopListFromFirebase()
                    Logger.d("lifecycle downloadNCTransportData saveStopListFromFirebase done")
                }
            ).joinAll()
        }
    }

    private suspend fun downloadGmbTransportData() {
        Logger.d("=== lifecycle downloadGmbTransportData start ===")
        gmbRepository.initDatabase()

        coroutineScope {
            listOf(
                launch {
                    gmbRepository.saveRouteListFromFirebase()
                    Logger.d("lifecycle downloadGmbTransportData saveRouteListFromFirebase done")
                },
                launch {
                    gmbRepository.saveRouteStopListFromFirebase()
                    Logger.d("lifecycle downloadGmbTransportData saveRouteStopListFromFirebase done")
                },
                launch {
                    gmbRepository.saveStopListFromFirebase()
                    Logger.d("lifecycle downloadGmbTransportData saveStopListFromFirebase done")
                }
            ).joinAll()
        }
    }

    private suspend fun downloadMTRTransportData() {
        Logger.d("=== lifecycle downloadMTRTransportData start ===")
        mtrRepository.initDatabase()

        coroutineScope {
            listOf(
                launch {
                    mtrRepository.saveRouteListFromFirebase()
                    Logger.d("lifecycle downloadMTRTransportData saveRouteListFromFirebase done")
                },
                launch {
                    mtrRepository.saveRouteStopListFromFirebase()
                    Logger.d("lifecycle downloadMTRTransportData saveRouteStopListFromFirebase done")
                },
                launch {
                    mtrRepository.saveStopListFromFirebase()
                    Logger.d("lifecycle downloadMTRTransportData saveStopListFromFirebase done")
                }
            ).joinAll()
        }
    }

    private suspend fun downloadLRTTransportData() {
        Logger.d("=== lifecycle downloadLRTTransportData start ===")
        lrtRepository.initDatabase()

        coroutineScope {
            listOf(
                launch {
                    lrtRepository.saveRouteListFromFirebase()
                    Logger.d("lifecycle downloadLRTTransportData saveRouteListFromFirebase done")
                },
                launch {
                    lrtRepository.saveRouteStopListFromFirebase()
                    Logger.d("lifecycle downloadLRTTransportData saveRouteStopListFromFirebase done")
                },
                launch {
                    lrtRepository.saveStopListFromFirebase()
                    Logger.d("lifecycle downloadLRTTransportData saveStopListFromFirebase done")
                }
            ).joinAll()
        }
    }

    private suspend fun downloadNLBTransportData() {
        Logger.d("=== lifecycle downloadNLBTransportData start ===")
        nlbRepository.initDatabase()

        coroutineScope {
            listOf(
                launch {
                    nlbRepository.saveRouteListFromFirebase()
                    Logger.d("lifecycle downloadNLBTransportData saveRouteListFromFirebase done")
                },
                launch {
                    nlbRepository.saveRouteStopListFromFirebase()
                    Logger.d("lifecycle downloadNLBTransportData saveRouteStopListFromFirebase done")
                },
                launch {
                    nlbRepository.saveStopListFromFirebase()
                    Logger.d("lifecycle downloadNLBTransportData saveStopListFromFirebase done")
                }
            ).joinAll()
        }
    }

    suspend fun resetTransportData() {
        kmbRepository.initDatabase()
        ncRepository.initDatabase()
        gmbRepository.initDatabase()
        mtrRepository.initDatabase()
        lrtRepository.initDatabase()
    }
}

enum class FetchTransportDataType {
    KMB, NC, GMB, MTR, LRT, NLB, ALL
}

class UpdateCheck(
    var kmb: Boolean = true,
    var nc: Boolean = true,
    var gmb: Boolean = true,
    var nlb: Boolean = true,
    var mtr: Boolean = true,
    var lrt: Boolean = true,
)