package hibernate.v2.sunshine.repository

import hibernate.v2.sunshine.model.RouteForRowAdapter
import hibernate.v2.sunshine.ui.settings.eta.add.AddEtaActivity
import java.util.EnumMap

enum class RouteAndStopListDataHolder {
    INSTANCE;

    private var hashMap: EnumMap<AddEtaActivity.EtaType, MutableList<RouteForRowAdapter>> =
        EnumMap(AddEtaActivity.EtaType::class.java)

    companion object {
        fun hasData(etaType: AddEtaActivity.EtaType) = !getData(etaType).isNullOrEmpty()

        fun setData(etaType: AddEtaActivity.EtaType, list: MutableList<RouteForRowAdapter>) {
            INSTANCE.hashMap[etaType] = list
        }

        fun getData(etaType: AddEtaActivity.EtaType): MutableList<RouteForRowAdapter>? {
            return INSTANCE.hashMap[etaType]
        }

        fun cleanData() {
            INSTANCE.hashMap = EnumMap(AddEtaActivity.EtaType::class.java)
        }
    }
}