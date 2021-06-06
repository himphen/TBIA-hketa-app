package hibernate.v2.sunshine.ui.settings.eta.add

import android.app.Application
import androidx.lifecycle.viewModelScope
import hibernate.v2.api.model.Bound
import hibernate.v2.sunshine.db.eta.EtaEntity
import hibernate.v2.sunshine.repository.EtaRepository
import hibernate.v2.sunshine.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddEtaViewModel(
    application: Application
) : BaseViewModel() {

    private val etaRepository = EtaRepository.getInstance(
        application.applicationContext
    )

    suspend fun getData(
        stopId: String,
        routeId: String,
        bound: Bound,
        serviceType: String,
        seq: String
    ) = withContext(Dispatchers.IO) {
        etaRepository.getData(
            stopId,
            routeId,
            bound,
            serviceType,
            seq
        )
    }

    suspend fun addData(item: EtaEntity) = withContext(Dispatchers.IO) {
        etaRepository.addData(item)
    }

    fun clearData(item: EtaEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                etaRepository.clearData(item)
            }
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                etaRepository.clearAllData()
            }
        }
    }
}
