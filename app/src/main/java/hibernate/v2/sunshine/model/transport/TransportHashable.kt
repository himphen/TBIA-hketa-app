package hibernate.v2.sunshine.model.transport

interface RouteHashable {
    fun routeHashId(): String
}

interface StopHashable {
    fun stopHashId(): String
}