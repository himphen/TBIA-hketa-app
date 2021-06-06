package hibernate.v2.sunshine.repository

import hibernate.v2.sunshine.model.RouteStopList

enum class RouteStopListDataHolder {
    INSTANCE;

    private var mObjectList: ArrayList<RouteStopList>? = null

    companion object {
        fun hasData(): Boolean {
            return INSTANCE.mObjectList != null
        }

        var data: ArrayList<RouteStopList>?
            get() {
                val retList = INSTANCE.mObjectList
                INSTANCE.mObjectList = null
                return retList
            }
            set(objectList) {
                INSTANCE.mObjectList = objectList
            }
    }
}