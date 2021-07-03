package hibernate.v2.sunshine.model

interface RouteHashable {
    fun routeHashId(): String
}

interface StopHashable {
    fun stopHashId(): String
}