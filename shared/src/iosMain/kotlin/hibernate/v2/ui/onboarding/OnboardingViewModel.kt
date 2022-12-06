package hibernate.v2.ui.onboarding

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
import hibernate.v2.utils.CommonLogger
import hibernate.v2.utils.logLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class OnboardingViewModel(
    private val fetchTransportDataRequired: (Int) -> Unit,
    private val fetchTransportDataCompleted: () -> Unit,
    private val fetchTransportDataCompletedCount: (Int) -> Unit
) : KoinComponent {

    private val sharedPreferencesManager: SharedPreferencesManager by inject()
    private val coreRepository: CoreRepository by inject()
    private val kmbInteractor: KmbInteractor by inject()
    private val ctbInteractor: CtbInteractor by inject()
    private val gmbInteractor: GmbInteractor by inject()
    private val mtrInteractor: MtrInteractor by inject()
    private val lrtInteractor: LrtInteractor by inject()
    private val nlbRepository: NlbInteractor by inject()

    @Suppress("MemberVisibilityCanBePrivate")
    val fetchTransportDataFailedList = mutableListOf<FailedCheckType>()
    var fetchTransportDataRequiredCount = 0

    suspend fun checkDbTransportData() {
        withContext(Dispatchers.Default) {
            try {
                var dataLoadingCount = 0
                var fetchTransportDataCompletedCountSum = 0

                fetchTransportDataFailedList.clear()

                val serverChecksum = try {
                    coreRepository.getChecksum()
                } catch (e: Exception) {
                    fetchTransportDataFailedList.add(FailedCheckType.CHECKSUM)
                    throw e
                }

                var newChecksum = Checksum()

                val existingChecksum: Checksum? = sharedPreferencesManager.transportDataChecksum

                val updateCheck = UpdateCheck()

                if (existingChecksum != null) {
                    if (serverChecksum.isValid() && serverChecksum == existingChecksum) {
                        fetchTransportDataRequired(dataLoadingCount)
                        return@withContext
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

                fetchTransportDataRequiredCount = dataLoadingCount
                fetchTransportDataRequired(dataLoadingCount)
                fetchTransportDataCompletedCount(0)

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
                            fetchTransportDataCompletedCount(
                                ++fetchTransportDataCompletedCountSum
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
                            fetchTransportDataCompletedCount(
                                ++fetchTransportDataCompletedCountSum
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
                            fetchTransportDataCompletedCount(
                                ++fetchTransportDataCompletedCountSum
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
                            fetchTransportDataCompletedCount(
                                ++fetchTransportDataCompletedCountSum
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
                            fetchTransportDataCompletedCount(
                                ++fetchTransportDataCompletedCountSum
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
                            fetchTransportDataCompletedCount(
                                ++fetchTransportDataCompletedCountSum
                            )
                        }
                    }
                ).awaitAll()

                sharedPreferencesManager.transportDataChecksum = newChecksum

                fetchTransportDataCompleted()
            } catch (e: Exception) {
                CommonLogger.e(e) { "Fetch transport data failed" }
                if (fetchTransportDataFailedList.isEmpty()) {
                    fetchTransportDataFailedList.add(FailedCheckType.OTHER)
                }
                fetchTransportDataCompleted()
            }
        }
    }

    private suspend fun downloadKmbTransportData() {
        logLifecycle("downloadKmbTransportData start")
        kmbInteractor.initDatabase()
        kmbInteractor.saveData()
        logLifecycle("downloadKmbTransportData end")
    }

    private suspend fun downloadCtbTransportData() {
        logLifecycle("downloadNCTransportData start")
        ctbInteractor.initDatabase()
        ctbInteractor.saveData()
        logLifecycle("downloadNCTransportData end")
    }

    private suspend fun downloadGmbTransportData() {
        logLifecycle("downloadGmbTransportData start")
        gmbInteractor.initDatabase()
        gmbInteractor.saveData()
        logLifecycle("downloadGmbTransportData end")
    }

    private suspend fun downloadMtrTransportData() {
        logLifecycle("downloadMTRTransportData start")
        mtrInteractor.initDatabase()
        mtrInteractor.saveData()
        logLifecycle("downloadMTRTransportData end")
    }

    private suspend fun downloadLrtTransportData() {
        logLifecycle("downloadLRTTransportData start")
        lrtInteractor.initDatabase()
        lrtInteractor.saveData()
        logLifecycle("downloadLRTTransportData end")
    }

    private suspend fun downloadNlbTransportData() {
        logLifecycle("downloadNLBTransportData start")
        nlbRepository.initDatabase()
        nlbRepository.saveData()
        logLifecycle("downloadNLBTransportData end")
    }

    suspend fun resetTransportData() {
        withContext(Dispatchers.Default) {
            sharedPreferencesManager.transportDataChecksum = null
            kmbInteractor.initDatabase()
            ctbInteractor.initDatabase()
            gmbInteractor.initDatabase()
            mtrInteractor.initDatabase()
            lrtInteractor.initDatabase()
            nlbRepository.initDatabase()
        }
    }
}