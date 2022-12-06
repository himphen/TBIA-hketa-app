package hibernate.v2.model.dataholder

import hibernate.v2.model.transport.eta.EtaType

enum class RouteAndStopListDataHolder {
    INSTANCE;

    private val hashMap = mutableMapOf<EtaType, MutableList<AddEtaRowItem>>()

    companion object {
        fun hasData(etaType: EtaType) = !getData(etaType).isNullOrEmpty()

        fun setData(etaType: EtaType, list: MutableList<AddEtaRowItem>) {
            INSTANCE.hashMap[etaType] = list
        }

        fun getData(etaType: EtaType): MutableList<AddEtaRowItem>? {
            return INSTANCE.hashMap[etaType]
        }

        fun cleanData() {
            INSTANCE.hashMap.clear()
        }
    }
}
