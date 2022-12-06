package hibernate.v2.domain.gmb

class GmbInteractor(
    val saveData: SaveData,
    val getRouteListDb: GetRouteListDb,
    val getRouteStopComponentListDb: GetRouteStopComponentListDb,
    val initDatabase: InitDatabase,
    val getStopListDb: GetStopListDb,
    val getRouteListFromStopId: GetRouteListFromStopId,
    val getRouteEtaCardList: GetRouteEtaCardList,
    val setMapRouteListIntoMapStop: SetMapRouteListIntoMapStop
)
