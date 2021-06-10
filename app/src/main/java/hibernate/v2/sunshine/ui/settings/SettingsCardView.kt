package hibernate.v2.sunshine.ui.settings

import android.content.Context
import android.view.LayoutInflater
import androidx.leanback.widget.BaseCardView
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.CardSettingsBinding

class SettingsCardView(context: Context) : BaseCardView(
    context,
    null,
    R.style.FullCardStyle
) {
    var viewBinding = CardSettingsBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        isFocusable = true
    }
}