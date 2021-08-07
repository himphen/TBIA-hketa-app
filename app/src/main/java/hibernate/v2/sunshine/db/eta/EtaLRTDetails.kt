package hibernate.v2.sunshine.db.eta

import androidx.room.Embedded
import hibernate.v2.sunshine.db.lrt.LRTStopEntity
import hibernate.v2.sunshine.model.Card
//data class EtaLRTDetails(
//    @Embedded
//    val savedEta: SavedEtaEntity,
//    @Embedded
//    val stop: LRTStopEntity,
//    @Embedded
//    val order: TrainEtaOrderEntity,
//) {
//    fun toSettingsEtaCard(): Card.SettingsTrainEtaItemCard {
//        return Card.SettingsTrainEtaItemCard(
//            entity = savedEta,
//            stop = stop.toTransportModel(),
//            position = order.position
//        )
//    }
//
//    fun toEtaCard(): Card.EtaTrainCard {
//        return Card.EtaTrainCard(
//            stop = stop.toTransportModelWithSeq(savedEta.seq),
//            position = order.position
//        )
//    }
//}