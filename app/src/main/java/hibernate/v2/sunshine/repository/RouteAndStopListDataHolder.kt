package hibernate.v2.sunshine.repository

import hibernate.v2.sunshine.model.RouteForRowAdapter

enum class RouteAndStopListDataHolder {
    INSTANCE;

    private var mObjectList: MutableList<RouteForRowAdapter>? = null

    companion object {
        fun hasData(): Boolean {
            return INSTANCE.mObjectList != null
        }

        var data: MutableList<RouteForRowAdapter>?
            get() {
                return INSTANCE.mObjectList
            }
            set(objectList) {
                INSTANCE.mObjectList = objectList
            }

        fun cleanData() {
            INSTANCE.mObjectList = null
        }
    }
}