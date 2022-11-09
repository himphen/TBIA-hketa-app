package hibernate.v2.model.dataholder

import hibernate.v2.model.transport.eta.EtaType
import hibernate.v2.model.transport.route.TransportRoute

enum class RouteListDataHolder {
    INSTANCE;

    private val hashMap: MutableMap<EtaType, List<TransportRoute>> =
        mutableMapOf()

    companion object {
        fun hasData(etaType: EtaType) = !getData(etaType).isNullOrEmpty()

        fun setData(etaType: EtaType, list: List<TransportRoute>) {
            INSTANCE.hashMap[etaType] = list
        }

        fun getData(etaType: EtaType): List<TransportRoute>? {
            return INSTANCE.hashMap[etaType]
        }

        fun cleanData() {
            INSTANCE.hashMap.clear()
        }
    }
}
