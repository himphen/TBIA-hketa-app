package hibernate.v2.model.transport.card

enum class EtaCardViewType(val value: Int) {
    Standard(0), Classic(1), Compact(2);

    companion object {
        fun from(value: Int?) = values().find { it.value == value } ?: Standard
    }
}
