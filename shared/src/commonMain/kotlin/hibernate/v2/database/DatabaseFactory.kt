package hibernate.v2.database

import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.db.SqlDriver
import hibernate.v2.api.model.transport.Bound
import hibernate.v2.api.model.transport.Company
import hibernate.v2.api.model.transport.GmbRegion
import hibernatev2database.Ctb_route
import hibernatev2database.Ctb_route_stop
import hibernatev2database.Gmb_route
import hibernatev2database.Gmb_route_stop
import hibernatev2database.Kmb_route
import hibernatev2database.Kmb_route_stop
import hibernatev2database.Lrt_route
import hibernatev2database.Lrt_route_stop
import hibernatev2database.Mtr_route
import hibernatev2database.Mtr_route_stop
import hibernatev2database.Saved_eta

object DatabaseFactory {
    fun createDatabase(driver: SqlDriver) = MyDatabase(
        driver,
        ctb_routeAdapter = Ctb_route.Adapter(
            ctb_route_boundAdapter = boundAdapter,
            ctb_route_companyAdapter = companyAdapter
        ),
        ctb_route_stopAdapter = Ctb_route_stop.Adapter(
            ctb_route_stop_boundAdapter = boundAdapter,
            ctb_route_stop_companyAdapter = companyAdapter
        ),
        gmb_routeAdapter = Gmb_route.Adapter(
            gmb_route_boundAdapter = boundAdapter,
            regionAdapter = gmbRegionAdapter
        ),
        gmb_route_stopAdapter = Gmb_route_stop.Adapter(
            gmb_route_stop_boundAdapter = boundAdapter
        ),
        kmb_routeAdapter = Kmb_route.Adapter(
            kmb_route_boundAdapter = boundAdapter
        ),
        mtr_routeAdapter = Mtr_route.Adapter(mtr_route_boundAdapter = boundAdapter),
        mtr_route_stopAdapter = Mtr_route_stop.Adapter(mtr_route_stop_boundAdapter = boundAdapter),
        lrt_routeAdapter = Lrt_route.Adapter(lrt_route_boundAdapter = boundAdapter),
        lrt_route_stopAdapter = Lrt_route_stop.Adapter(
            lrt_route_stop_boundAdapter = boundAdapter
        ),
        kmb_route_stopAdapter = Kmb_route_stop.Adapter(
            kmb_route_stop_boundAdapter = boundAdapter
        ),
        saved_etaAdapter = Saved_eta.Adapter(
            saved_eta_companyAdapter = companyAdapter,
            saved_eta_route_boundAdapter = boundAdapter
        ),
    )

    private val companyAdapter = object : ColumnAdapter<Company, String> {
        override fun decode(databaseValue: String) = Company.from(databaseValue)

        override fun encode(value: Company) = value.value
    }

    private val boundAdapter = object : ColumnAdapter<Bound, String> {
        override fun decode(databaseValue: String) = Bound.from(databaseValue)

        override fun encode(value: Bound) = value.value
    }

    private val gmbRegionAdapter = object : ColumnAdapter<GmbRegion, String> {
        override fun decode(databaseValue: String) = GmbRegion.from(databaseValue)

        override fun encode(value: GmbRegion) = value.value
    }
}