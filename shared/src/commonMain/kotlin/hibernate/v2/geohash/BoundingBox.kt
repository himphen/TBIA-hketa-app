package hibernate.v2.geohash

import kotlin.math.max
import kotlin.math.min

open class BoundingBox(y1: Double, y2: Double, x1: Double, x2: Double) {

    var minLat: Double = 0.0
    var maxLat: Double = 0.0
    var minLon: Double = 0.0
    var maxLon: Double = 0.0

    val topLeft: Location
        get() = Location().apply {
            latitude = maxLat
            longitude = minLon
        }

    val topRight: Location
        get() = Location().apply {
            latitude = maxLat
            longitude = maxLon
        }

    val bottomLeft: Location
        get() = Location().apply {
            latitude = minLat
            longitude = minLon
        }

    val bottomRight: Location
        get() = Location().apply {
            latitude = minLat
            longitude = maxLon
        }

    val center: Location
        get() = Location().apply {
            latitude = (minLat + maxLat) / 2
            longitude = (minLon + maxLon) / 2
        }

    val geoHash
        get() = GeoHash(center)

    constructor(p1: Location, p2: Location) : this(
        p1.latitude,
        p2.latitude,
        p1.longitude,
        p2.longitude
    )

    init {
        minLon = min(x1, x2)
        maxLon = max(x1, x2)
        minLat = min(y1, y2)
        maxLat = max(y1, y2)
    }

    operator fun contains(point: Location) = point.latitude >= minLat && point.longitude >= minLon
        && point.latitude <= maxLat && point.longitude <= maxLon

    fun intersects(other: BoundingBox) = !(other.minLon > maxLon || other.maxLon < minLon
        || other.minLat > maxLat || other.maxLat < minLat)
}