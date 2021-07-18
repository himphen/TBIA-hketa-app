package hibernate.v2.sunshine.repository

import hibernate.v2.sunshine.ui.settings.eta.add.AddEtaViewModel
import hibernate.v2.sunshine.ui.settings.eta.add.leanback.RouteForRowAdapter
import java.util.EnumMap

enum class RouteAndStopListDataHolder {
    INSTANCE;

    private var hashMap: EnumMap<AddEtaViewModel.EtaType, MutableList<RouteForRowAdapter>> =
        EnumMap(AddEtaViewModel.EtaType::class.java)

    companion object {
        fun hasData(etaType: AddEtaViewModel.EtaType) = !getData(etaType).isNullOrEmpty()

        fun setData(etaType: AddEtaViewModel.EtaType, list: MutableList<RouteForRowAdapter>) {
            INSTANCE.hashMap[etaType] = list
        }

        fun getData(etaType: AddEtaViewModel.EtaType): MutableList<RouteForRowAdapter>? {
            return INSTANCE.hashMap[etaType]
        }

        fun cleanData() {
            INSTANCE.hashMap = EnumMap(AddEtaViewModel.EtaType::class.java)
        }
    }
}