package hibernate.v2.sunshine.ui.onboarding

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.himphen.logger.Logger
import hibernate.v2.api.model.transport.Checksum
import hibernate.v2.sunshine.core.SharedPreferencesManager
import hibernate.v2.sunshine.domain.gmb.GmbInteractor
import hibernate.v2.sunshine.repository.CoreRepository
import hibernate.v2.sunshine.repository.CtbRepository
import hibernate.v2.sunshine.repository.KmbRepository
import hibernate.v2.sunshine.repository.LRTRepository
import hibernate.v2.sunshine.repository.MTRRepository
import hibernate.v2.sunshine.repository.NLBRepository
import hibernate.v2.sunshine.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class OnboardingViewModel(
    private val sharedPreferencesManager: SharedPreferencesManager,
    private val coreRepository: CoreRepository,
    private val kmbRepository: KmbRepository,
    private val ctbRepository: CtbRepository,
    private val gmbInteractor: GmbInteractor,
    private val mtrRepository: MTRRepository,
    private val lrtRepository: LRTRepository,
    private val nlbRepository: NLBRepository,
) : BaseViewModel() {

    val fetchTransportDataRequired = MutableLiveData(-1)
    val fetchTransportDataCannotInit = MutableLiveData<Unit>()
    val fetchTransportDataCompleted = MutableLiveData<Unit>()
    val fetchTransportDataCompletedCount = MutableLiveData(0)
    val fetchTransportDataFailedList = arrayListOf<FailedCheckType>()

    fun checkDbTransportData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                initRemoteConfig()

                var dataLoadingCount = 0

                val serverChecksum = try {
                    coreRepository.getChecksum()
                } catch (e: Exception) {
                    fetchTransportDataFailedList.add(FailedCheckType.CHECKSUM)
                    throw e
                }

                var newChecksum = Checksum()

                val existingChecksum = sharedPreferencesManager.transportDataChecksum

                val updateCheck = UpdateCheck()

                if (existingChecksum != null) {
                    if (serverChecksum.isValid() && serverChecksum == existingChecksum) {
                        fetchTransportDataRequired.postValue(dataLoadingCount)
                        return@launch
                    }

                    updateCheck.kmb =
                        serverChecksum.kmb == null || serverChecksum.kmb != existingChecksum.kmb
                    updateCheck.ctb =
                        serverChecksum.ctb == null || serverChecksum.ctb != existingChecksum.ctb
                    updateCheck.gmb =
                        serverChecksum.gmb == null || serverChecksum.gmb != existingChecksum.gmb
                    updateCheck.mtr =
                        serverChecksum.mtr == null || serverChecksum.mtr != existingChecksum.mtr
                    updateCheck.lrt =
                        serverChecksum.lrt == null || serverChecksum.lrt != existingChecksum.lrt
                    updateCheck.nlb =
                        serverChecksum.nlb == null || serverChecksum.nlb != existingChecksum.nlb

                    newChecksum = existingChecksum.copy()

                    if (updateCheck.kmb) dataLoadingCount++
                    if (updateCheck.ctb) dataLoadingCount++
                    if (updateCheck.gmb) dataLoadingCount++
                    if (updateCheck.mtr) dataLoadingCount++
                    if (updateCheck.lrt) dataLoadingCount++
                    if (updateCheck.nlb) dataLoadingCount++
                } else {
                    dataLoadingCount = 6
                }

                fetchTransportDataRequired.postValue(dataLoadingCount)
                fetchTransportDataCompletedCount.postValue(0)

                listOf(
                    async {
                        if (updateCheck.kmb) {
                            try {
                                downloadKmbTransportData()
                                newChecksum.kmb = serverChecksum.kmb
                            } catch (e: Exception) {
                                Logger.t("lifecycle").e(e, "downloadKmbTransportData error")
                                fetchTransportDataFailedList.add(FailedCheckType.KMB)
                            }
                            fetchTransportDataCompletedCount.postValue(
                                (fetchTransportDataCompletedCount.value ?: 0) + 1
                            )
                        }
                    },
                    async {
                        if (updateCheck.ctb) {
                            try {
                                downloadCtbTransportData()
                                newChecksum.ctb = serverChecksum.ctb
                            } catch (e: Exception) {
                                Logger.t("lifecycle").e(e, "downloadNCTransportData error")
                                fetchTransportDataFailedList.add(FailedCheckType.CTB)
                            }
                            fetchTransportDataCompletedCount.postValue(
                                (fetchTransportDataCompletedCount.value ?: 0) + 1
                            )
                        }
                    },
                    async {
                        if (updateCheck.gmb) {
                            try {
                                downloadGmbTransportData()
                                newChecksum.gmb = serverChecksum.gmb
                            } catch (e: Exception) {
                                Logger.t("lifecycle").e(e, "downloadGmbTransportData error")
                                fetchTransportDataFailedList.add(FailedCheckType.GMB)
                            }
                            fetchTransportDataCompletedCount.postValue(
                                (fetchTransportDataCompletedCount.value ?: 0) + 1
                            )
                        }
                    },
                    async {
                        if (updateCheck.mtr) {
                            try {
                                downloadMtrTransportData()
                                newChecksum.mtr = serverChecksum.mtr
                            } catch (e: Exception) {
                                Logger.t("lifecycle").e(e, "downloadMTRTransportData error")
                                fetchTransportDataFailedList.add(FailedCheckType.MTR)
                            }
                            fetchTransportDataCompletedCount.postValue(
                                (fetchTransportDataCompletedCount.value ?: 0) + 1
                            )
                        }
                    },
                    async {
                        if (updateCheck.lrt) {
                            try {
                                downloadLrtTransportData()
                                newChecksum.lrt = serverChecksum.lrt
                            } catch (e: Exception) {
                                Logger.t("lifecycle").e(e, "downloadLRTTransportData error")
                                fetchTransportDataFailedList.add(FailedCheckType.LRT)
                            }
                            fetchTransportDataCompletedCount.postValue(
                                (fetchTransportDataCompletedCount.value ?: 0) + 1
                            )
                        }
                    },
                    async {
                        if (updateCheck.nlb) {
                            try {
                                downloadNlbTransportData()
                                newChecksum.nlb = serverChecksum.nlb
                            } catch (e: Exception) {
                                Logger.t("lifecycle").e(e, "downloadNLBTransportData error")
                                fetchTransportDataFailedList.add(FailedCheckType.NLB)
                            }
                            fetchTransportDataCompletedCount.postValue(
                                (fetchTransportDataCompletedCount.value ?: 0) + 1
                            )
                        }
                    }
                ).awaitAll()

                sharedPreferencesManager.transportDataChecksum = newChecksum

                fetchTransportDataCompleted.postValue(Unit)
            } catch (e: Exception) {
                Logger.e(e, "Fetch transport data failed")
                if (fetchTransportDataFailedList.isEmpty()) {
                    fetchTransportDataFailedList.add(FailedCheckType.OTHER)
                }
                fetchTransportDataCompleted.postValue(Unit)
            }
        }
    }

    private suspend fun initRemoteConfig() {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        Firebase.remoteConfig.setConfigSettingsAsync(configSettings).await()
        Firebase.remoteConfig.setDefaultsAsync(mapOf("api_base_url_v1" to "")).await()
        Firebase.remoteConfig.fetchAndActivate().await()
    }

    private suspend fun downloadKmbTransportData() {
        Logger.t("lifecycle").d("downloadKmbTransportData start")
        kmbRepository.initDatabase()
        kmbRepository.saveData()
    }

    private suspend fun downloadCtbTransportData() {
        Logger.t("lifecycle").d("downloadNCTransportData start")
        ctbRepository.initDatabase()
        ctbRepository.saveData()
    }

    private suspend fun downloadGmbTransportData() {
        Logger.t("lifecycle").d("downloadGmbTransportData start")
        gmbInteractor.initDatabase()
        gmbInteractor.saveData()
    }

    private suspend fun downloadMtrTransportData() {
        Logger.t("lifecycle").d("downloadMTRTransportData start")
        mtrRepository.initDatabase()
        mtrRepository.saveData()
    }

    private suspend fun downloadLrtTransportData() {
        Logger.t("lifecycle").d("downloadLRTTransportData start")
        lrtRepository.initDatabase()
        lrtRepository.saveData()
    }

    private suspend fun downloadNlbTransportData() {
        Logger.t("lifecycle").d("downloadNLBTransportData start")
        nlbRepository.initDatabase()
        nlbRepository.saveData()
    }

    suspend fun resetTransportData() {
        sharedPreferencesManager.transportDataChecksum = null
        kmbRepository.initDatabase()
        ctbRepository.initDatabase()
        gmbInteractor.initDatabase()
        mtrRepository.initDatabase()
        lrtRepository.initDatabase()
        nlbRepository.initDatabase()
    }
}

enum class FailedCheckType {
    KMB, CTB, GMB, MTR, LRT, NLB, OTHER, CHECKSUM
}

class UpdateCheck(
    var kmb: Boolean = true,
    var ctb: Boolean = true,
    var gmb: Boolean = true,
    var nlb: Boolean = true,
    var mtr: Boolean = true,
    var lrt: Boolean = true,
)
