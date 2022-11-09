package hibernate.v2.ui.route

import hibernate.v2.domain.ctb.CtbInteractor
import hibernate.v2.domain.gmb.GmbInteractor
import hibernate.v2.domain.kmb.KmbInteractor
import hibernate.v2.domain.lrt.LrtInteractor
import hibernate.v2.domain.mtr.MtrInteractor
import hibernate.v2.domain.nlb.NlbInteractor
import hibernate.v2.model.transport.TransportStop
import hibernate.v2.model.transport.eta.EtaType
import hibernate.v2.model.transport.route.TransportRoute
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RouteListMobileViewModel(
    private val filteredTransportRouteList: (Pair<EtaType, List<TransportRoute>>) -> Unit,
    private val stopList: (List<TransportStop>) -> Unit,
    private val tabItemSelected: () -> Unit,
    private val selectedEtaTypeUpdated: () -> Unit,
    private val searchRouteKeywordUpdated: () -> Unit,
) : KoinComponent {

    private val kmbInteractor: KmbInteractor by inject()
    private val ctbInteractor: CtbInteractor by inject()
    private val gmbInteractor: GmbInteractor by inject()
    private val mtrInteractor: MtrInteractor by inject()
    private val lrtInteractor: LrtInteractor by inject()
    private val nlbRepository: NlbInteractor by inject()

    val tabItemSelectedLiveData: EtaType? = null
    var selectedEtaType: EtaType = EtaType.KMB

    val searchRouteKeyword: String = ""

}
