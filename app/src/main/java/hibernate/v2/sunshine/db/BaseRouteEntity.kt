package hibernate.v2.sunshine.db

import androidx.room.Ignore
import hibernate.v2.sunshine.model.transport.route.RouteComponent

abstract class BaseRouteEntity {
    @Ignore
    var routeComponent: RouteComponent = RouteComponent()

    fun parseRouteNumber(routeNo: String) {
        if (routeComponent.parsed) return

        Regex("([A-Z]*)(\\d+)([A-Z]*)").find(routeNo)?.let { match ->
            routeComponent.routePrefix = match.destructured.component1()
            routeComponent.routeNumber = match.destructured.component2().toInt()
            routeComponent.routeSuffix = match.destructured.component3()
        }

        routeComponent.parsed = true
    }
}
