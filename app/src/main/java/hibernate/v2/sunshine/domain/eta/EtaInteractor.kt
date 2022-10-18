package hibernate.v2.sunshine.domain.eta

class EtaInteractor(
    val getKmbStopEtaApi: GetKmbStopEtaApi,
    val getCtbStopEtaApi: GetCtbStopEtaApi,
    val getGmbStopEtaApi: GetGmbStopEtaApi,
    val getMTRStopEtaApi: GetMTRStopEtaApi,
    val getLRTStopEtaApi: GetLrtStopEtaApi,
    val getNLBStopEtaApi: GetNLBStopEtaApi,
    val getSavedKmbEtaList: GetSavedKmbEtaList,
    val getSavedNCEtaList: GetSavedNCEtaList,
    val getSavedGmbEtaList: GetSavedGmbEtaList,
    val getSavedMTREtaList: GetSavedMTREtaList,
    val getSavedLrtEtaList: GetSavedLRTEtaList,
    val getSavedNLBEtaList: GetSavedNLBEtaList,
    val hasEtaInDb: HasEtaInDb,
    val addEta: AddEta,
    val clearEta: ClearEta,
    val clearAllEta: ClearAllEta,
    val getEtaOrderList: GetEtaOrderList,
    val updateEtaOrderList: UpdateEtaOrderList,
)
