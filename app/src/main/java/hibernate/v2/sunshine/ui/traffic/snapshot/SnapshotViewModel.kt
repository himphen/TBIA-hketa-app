package hibernate.v2.sunshine.ui.traffic.snapshot

import hibernate.v2.sunshine.repository.TrafficRepository
import hibernate.v2.sunshine.ui.base.BaseViewModel

class SnapshotViewModel(private val repo: TrafficRepository) : BaseViewModel() {
    suspend fun getSnapshotCameraList() = repo.getSnapshotCameraListDb()
}