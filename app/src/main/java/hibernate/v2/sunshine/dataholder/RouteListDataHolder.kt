package hibernate.v2.sunshine.dataholder

import hibernate.v2.model.transport.eta.EtaType
import hibernate.v2.model.transport.route.TransportRoute
import java.util.EnumMap

enum class RouteListDataHolder {
    INSTANCE;

    private var hashMap: EnumMap<EtaType, List<TransportRoute>> =
        EnumMap(EtaType::class.java)

    companion object {
        fun hasData(etaType: EtaType) = !getData(etaType).isNullOrEmpty()

        fun setData(etaType: EtaType, list: List<TransportRoute>) {
            INSTANCE.hashMap[etaType] = list
        }

        fun getData(etaType: EtaType): List<TransportRoute>? {
            return INSTANCE.hashMap[etaType]
        }

        fun cleanData() {
            INSTANCE.hashMap = EnumMap(EtaType::class.java)
        }
    }
}
