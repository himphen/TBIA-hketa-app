package hibernate.v2.tbia.ui.onboarding

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.himphen.logger.Logger
import hibernate.v2.api.model.transport.Checksum
import hibernate.v2.api.repository.CoreRepository
import hibernate.v2.core.SharedPreferencesManager
import hibernate.v2.domain.ctb.CtbInteractor
import hibernate.v2.domain.gmb.GmbInteractor
import hibernate.v2.domain.kmb.KmbInteractor
import hibernate.v2.domain.lrt.LrtInteractor
import hibernate.v2.domain.mtr.MtrInteractor
import hibernate.v2.domain.nlb.NlbInteractor
import hibernate.v2.model.checksum.FailedCheckType
import hibernate.v2.model.checksum.UpdateCheck
import hibernate.v2.tbia.ui.base.BaseViewModel
import hibernate.v2.utils.logLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val sharedPreferencesManager: SharedPreferencesManager,
    private val coreRepository: CoreRepository,
    private val kmbInteractor: KmbInteractor,
    private val ctbInteractor: CtbInteractor,
    private val gmbInteractor: GmbInteractor,
    private val mtrInteractor: MtrInteractor,
    private val lrtInteractor: LrtInteractor,
    private val nlbRepository: NlbInteractor,
) : BaseViewModel() {

    val fetchTransportDataRequired = MutableLiveData(-1)
    val fetchTransportDataCannotInit = MutableLiveData<Unit>()
    val fetchTransportDataCompleted = MutableLiveData<Unit>()
    val fetchTransportDataCompletedCount = MutableLiveData(0)
    val fetchTransportDataFailedList = arrayListOf<FailedCheckType>()

    fun checkDbTransportData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
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
                                logLifecycle(e, "downloadKmbTransportData error")
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
                                logLifecycle(e, "downloadNCTransportData error")
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
                                logLifecycle(e, "downloadGmbTransportData error")
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
                                logLifecycle(e, "downloadMTRTransportData error")
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
                                logLifecycle(e, "downloadLRTTransportData error")
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
                                logLifecycle(e, "downloadNLBTransportData error")
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

    private suspend fun downloadKmbTransportData() {
        logLifecycle("downloadKmbTransportData start")
        kmbInteractor.initDatabase()
        kmbInteractor.saveData()
    }

    private suspend fun downloadCtbTransportData() {
        logLifecycle("downloadNCTransportData start")
        ctbInteractor.initDatabase()
        ctbInteractor.saveData()
    }

    private suspend fun downloadGmbTransportData() {
        logLifecycle("downloadGmbTransportData start")
        gmbInteractor.initDatabase()
        gmbInteractor.saveData()
    }

    private suspend fun downloadMtrTransportData() {
        logLifecycle("downloadMTRTransportData start")
        mtrInteractor.initDatabase()
        mtrInteractor.saveData()
    }

    private suspend fun downloadLrtTransportData() {
        logLifecycle("downloadLRTTransportData start")
        lrtInteractor.initDatabase()
        lrtInteractor.saveData()
    }

    private suspend fun downloadNlbTransportData() {
        logLifecycle("downloadNLBTransportData start")
        nlbRepository.initDatabase()
        nlbRepository.saveData()
    }

    suspend fun resetTransportData() {
        sharedPreferencesManager.transportDataChecksum = null
        kmbInteractor.initDatabase()
        ctbInteractor.initDatabase()
        gmbInteractor.initDatabase()
        mtrInteractor.initDatabase()
        lrtInteractor.initDatabase()
        nlbRepository.initDatabase()
    }
}
