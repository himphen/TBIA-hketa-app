package hibernate.v2.sunshine.repository

import hibernate.v2.sunshine.model.transport.EtaType
import hibernate.v2.sunshine.model.RouteForRowAdapter
import java.util.EnumMap

enum class RouteAndStopListDataHolder {
    INSTANCE;

    private var hashMap: EnumMap<EtaType, MutableList<RouteForRowAdapter>> =
        EnumMap(EtaType::class.java)

    companion object {
        fun hasData(etaType: EtaType) = !getData(etaType).isNullOrEmpty()

        fun setData(etaType: EtaType, list: MutableList<RouteForRowAdapter>) {
            INSTANCE.hashMap[etaType] = list
        }

        fun getData(etaType: EtaType): MutableList<RouteForRowAdapter>? {
            return INSTANCE.hashMap[etaType]
        }

        fun cleanData() {
            INSTANCE.hashMap = EnumMap(EtaType::class.java)
        }
    }
}