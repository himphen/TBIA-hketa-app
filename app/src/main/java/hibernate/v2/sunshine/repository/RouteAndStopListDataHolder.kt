package hibernate.v2.sunshine.repository

import hibernate.v2.sunshine.model.AddEtaRowItem
import hibernate.v2.sunshine.model.transport.eta.EtaType
import java.util.EnumMap

enum class RouteAndStopListDataHolder {
    INSTANCE;

    private var hashMap: EnumMap<EtaType, MutableList<AddEtaRowItem>> =
        EnumMap(EtaType::class.java)

    companion object {
        fun hasData(etaType: EtaType) = !getData(etaType).isNullOrEmpty()

        fun setData(etaType: EtaType, list: MutableList<AddEtaRowItem>) {
            INSTANCE.hashMap[etaType] = list
        }

        fun getData(etaType: EtaType): MutableList<AddEtaRowItem>? {
            return INSTANCE.hashMap[etaType]
        }

        fun cleanData() {
            INSTANCE.hashMap = EnumMap(EtaType::class.java)
        }
    }
}
