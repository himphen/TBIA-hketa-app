package hibernate.v2.tbia.ui.bookmark.home.leanback.view

import android.content.Context
import android.util.AttributeSet
import android.view.View.OnFocusChangeListener
import androidx.leanback.widget.BaseCardView
import androidx.viewbinding.ViewBinding
import hibernate.v2.model.Card
import hibernate.v2.tbia.R

abstract class BaseEtaCardView<T : ViewBinding>(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.style.FullCardStyle,
) : BaseCardView(
    context,
    attrs,
    defStyleAttr
) {
    abstract var viewBinding: T

    abstract fun onBind(card: Card.EtaCard)

    init {
        isFocusable = true
        onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                setBackgroundColor(context.getColor(R.color.eta_card_background_selected))
            } else {
                setBackgroundColor(context.getColor(R.color.eta_card_background))
            }
        }
    }
}
