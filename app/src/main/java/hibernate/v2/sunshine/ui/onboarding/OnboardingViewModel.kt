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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

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

    val fetchTransportDataRequired = MutableLiveData(-1)
    val fetchTransportDataCompleted = MutableLiveData<Unit>()
    val fetchTransportDataCompletedCount = MutableLiveData(0)
    val fetchTransportDataFailedList = arrayListOf<FetchTransportDataType>()

    fun checkDbTransportData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                var dataLoadingCount = 0
                val checksum = coreRepository.getChecksum()
                val existingChecksum = sharedPreferencesManager.transportDataChecksum

                val updateCheck = UpdateCheck()

                if (checksum != null && existingChecksum != null) {
                    if (checksum == existingChecksum) {
                        fetchTransportDataRequired.postValue(dataLoadingCount)
                        return@launch
                    }

                    updateCheck.kmb = checksum.kmb != existingChecksum.kmb
                    updateCheck.nc = checksum.citybusNwfb != existingChecksum.citybusNwfb
                    updateCheck.gmb = checksum.gmb != existingChecksum.gmb
                    updateCheck.mtr = checksum.mtr != existingChecksum.mtr
                    updateCheck.lrt = checksum.lrt != existingChecksum.lrt
                    updateCheck.nlb = checksum.nlb != existingChecksum.nlb

                    if (updateCheck.kmb) dataLoadingCount++
                    if (updateCheck.nc) dataLoadingCount++
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
                            } catch (e: Exception) {
                                Logger.t("lifecycle").e(e, "downloadKmbTransportData error")
                                fetchTransportDataFailedList.add(FetchTransportDataType.KMB)
                            }
                            fetchTransportDataCompletedCount.postValue(
                                (fetchTransportDataCompletedCount.value ?: 0) + 1
                            )
                        }
                    },
                    async {
                        if (updateCheck.nc) {
                            try {
                                downloadNCTransportData()
                            } catch (e: Exception) {
                                Logger.t("lifecycle").e(e, "downloadNCTransportData error")
                                fetchTransportDataFailedList.add(FetchTransportDataType.NC)
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
                            } catch (e: Exception) {
                                Logger.t("lifecycle").e(e, "downloadGmbTransportData error")
                                fetchTransportDataFailedList.add(FetchTransportDataType.GMB)
                            }
                            fetchTransportDataCompletedCount.postValue(
                                (fetchTransportDataCompletedCount.value ?: 0) + 1
                            )
                        }
                    },
                    async {
                        if (updateCheck.mtr) {
                            try {
                                downloadMTRTransportData()
                            } catch (e: Exception) {
                                Logger.t("lifecycle").e(e, "downloadMTRTransportData error")
                                fetchTransportDataFailedList.add(FetchTransportDataType.MTR)
                            }
                            fetchTransportDataCompletedCount.postValue(
                                (fetchTransportDataCompletedCount.value ?: 0) + 1
                            )
                        }
                    },
                    async {
                        if (updateCheck.lrt) {
                            try {
                                downloadLRTTransportData()
                            } catch (e: Exception) {
                                Logger.t("lifecycle").e(e, "downloadLRTTransportData error")
                                fetchTransportDataFailedList.add(FetchTransportDataType.LRT)
                            }
                            fetchTransportDataCompletedCount.postValue(
                                (fetchTransportDataCompletedCount.value ?: 0) + 1
                            )
                        }
                    },
                    async {
                        if (updateCheck.nlb) {
                            try {
                                downloadNLBTransportData()
                            } catch (e: Exception) {
                                Logger.t("lifecycle").e(e, "downloadNLBTransportData error")
                                fetchTransportDataFailedList.add(FetchTransportDataType.NLB)
                            }
                            fetchTransportDataCompletedCount.postValue(
                                (fetchTransportDataCompletedCount.value ?: 0) + 1
                            )
                        }
                    }
                ).awaitAll()

                sharedPreferencesManager.transportDataChecksum = checksum
                fetchTransportDataCompleted.postValue(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                fetchTransportDataFailedList.add(FetchTransportDataType.ALL)
                fetchTransportDataCompleted.postValue(Unit)
            }
        }
    }

    private suspend fun downloadKmbTransportData() {
        Logger.t("lifecycle").d("downloadKmbTransportData start")
        kmbRepository.initDatabase()

        supervisorScope {
            listOf(
                async(Dispatchers.IO) {
                    kmbRepository.saveStopListFromFirebase()
                    Logger.t("lifecycle").d("downloadKmbTransportData saveStopListFromFirebase done")
                },
                async(Dispatchers.IO) {
                    kmbRepository.saveRouteListFromFirebase()
                    Logger.t("lifecycle").d("downloadKmbTransportData saveRouteListFromFirebase done")
                },
                async(Dispatchers.IO) {
                    kmbRepository.saveRouteStopListFromFirebase()
                    Logger.t("lifecycle").d("downloadKmbTransportData saveRouteStopListFromFirebase done")
                }
            ).awaitAll()
        }
    }

    private suspend fun downloadNCTransportData() {
        Logger.t("lifecycle").d("downloadNCTransportData start")
        ncRepository.initDatabase()

        supervisorScope {
            listOf(
                async(Dispatchers.IO) {
                    ncRepository.saveRouteListFromFirebase()
                    Logger.t("lifecycle")
                        .d("downloadNCTransportData saveRouteListFromFirebase done")
                },
                async(Dispatchers.IO) {
                    ncRepository.saveRouteStopListFromFirebase()
                    Logger.t("lifecycle")
                        .d("downloadNCTransportData saveRouteStopListFromFirebase done")
                },
                async(Dispatchers.IO) {
                    ncRepository.saveStopListFromFirebase()
                    Logger.t("lifecycle").d("downloadNCTransportData saveStopListFromFirebase done")
                }
            ).awaitAll()
        }
    }

    private suspend fun downloadGmbTransportData() {
        Logger.t("lifecycle").d("downloadGmbTransportData start")
        gmbRepository.initDatabase()

        supervisorScope {
            listOf(
                async(Dispatchers.IO) {
                    gmbRepository.saveRouteListFromFirebase()
                    Logger.t("lifecycle")
                        .d("downloadGmbTransportData saveRouteListFromFirebase done")
                },
                async(Dispatchers.IO) {
                    gmbRepository.saveRouteStopListFromFirebase()
                    Logger.t("lifecycle")
                        .d("downloadGmbTransportData saveRouteStopListFromFirebase done")
                },
                async(Dispatchers.IO) {
                    gmbRepository.saveStopListFromFirebase()
                    Logger.t("lifecycle")
                        .d("downloadGmbTransportData saveStopListFromFirebase done")
                }
            ).awaitAll()
        }
    }

    private suspend fun downloadMTRTransportData() {
        Logger.t("lifecycle").d("downloadMTRTransportData start")
        mtrRepository.initDatabase()

        supervisorScope {
            listOf(
                async(Dispatchers.IO) {
                    mtrRepository.saveRouteListFromFirebase()
                    Logger.t("lifecycle")
                        .d("downloadMTRTransportData saveRouteListFromFirebase done")
                },
                async(Dispatchers.IO) {
                    mtrRepository.saveRouteStopListFromFirebase()
                    Logger.t("lifecycle")
                        .d("downloadMTRTransportData saveRouteStopListFromFirebase done")
                },
                async(Dispatchers.IO) {
                    mtrRepository.saveStopListFromFirebase()
                    Logger.t("lifecycle")
                        .d("downloadMTRTransportData saveStopListFromFirebase done")
                }
            ).awaitAll()
        }
    }

    private suspend fun downloadLRTTransportData() {
        Logger.t("lifecycle").d("downloadLRTTransportData start")
        lrtRepository.initDatabase()

        supervisorScope {
            listOf(
                async(Dispatchers.IO) {
                    lrtRepository.saveRouteListFromFirebase()
                    Logger.t("lifecycle")
                        .d("downloadLRTTransportData saveRouteListFromFirebase done")
                },
                async(Dispatchers.IO) {
                    lrtRepository.saveRouteStopListFromFirebase()
                    Logger.t("lifecycle")
                        .d("downloadLRTTransportData saveRouteStopListFromFirebase done")
                },
                async(Dispatchers.IO) {
                    lrtRepository.saveStopListFromFirebase()
                    Logger.t("lifecycle")
                        .d("downloadLRTTransportData saveStopListFromFirebase done")
                }
            ).awaitAll()
        }
    }

    private suspend fun downloadNLBTransportData() {
        Logger.t("lifecycle").d("downloadNLBTransportData start")
        nlbRepository.initDatabase()

        supervisorScope {
            listOf(
                async(Dispatchers.IO) {
                    nlbRepository.saveRouteListFromFirebase()
                    Logger.t("lifecycle")
                        .d("downloadNLBTransportData saveRouteListFromFirebase done")
                },
                async(Dispatchers.IO) {
                    nlbRepository.saveRouteStopListFromFirebase()
                    Logger.t("lifecycle")
                        .d("downloadNLBTransportData saveRouteStopListFromFirebase done")
                },
                async(Dispatchers.IO) {
                    nlbRepository.saveStopListFromFirebase()
                    Logger.t("lifecycle")
                        .d("downloadNLBTransportData saveStopListFromFirebase done")
                }
            ).awaitAll()
        }
    }

    suspend fun resetTransportData() {
        sharedPreferencesManager.transportDataChecksum = null
        kmbRepository.initDatabase()
        ncRepository.initDatabase()
        gmbRepository.initDatabase()
        mtrRepository.initDatabase()
        lrtRepository.initDatabase()
        nlbRepository.initDatabase()
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
