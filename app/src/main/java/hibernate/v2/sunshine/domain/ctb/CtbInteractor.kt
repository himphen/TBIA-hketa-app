package hibernate.v2.sunshine.domain.ctb

class CtbInteractor(
    val saveData: SaveData,
    val getRouteListDb: GetRouteListDb,
    val getRouteStopComponentListDb: GetRouteStopComponentListDb,
    val initDatabase: InitDatabase,
    val getStopListDb: GetStopListDb,
    val getRouteListFromStopId: GetRouteListFromStopId,
    val getRouteEtaCardList: GetRouteEtaCardList,
    val setMapRouteListIntoMapStop: SetMapRouteListIntoMapStop
)