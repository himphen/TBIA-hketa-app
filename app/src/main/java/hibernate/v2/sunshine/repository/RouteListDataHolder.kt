package hibernate.v2.sunshine.repository

import hibernate.v2.sunshine.model.transport.EtaType
import hibernate.v2.sunshine.model.transport.TransportRoute
import java.util.EnumMap

enum class RouteListDataHolder {
    INSTANCE;

    private var hashMap: EnumMap<EtaType, MutableList<TransportRoute>> =
        EnumMap(EtaType::class.java)

    companion object {
        fun hasData(etaType: EtaType) = !getData(etaType).isNullOrEmpty()

        fun setData(etaType: EtaType, list: MutableList<TransportRoute>) {
            INSTANCE.hashMap[etaType] = list
        }

        fun getData(etaType: EtaType): MutableList<TransportRoute>? {
            return INSTANCE.hashMap[etaType]
        }

        fun cleanData() {
            INSTANCE.hashMap = EnumMap(EtaType::class.java)
        }
    }
}
