package hibernate.v2.sunshine.ui.settings.leanback

import android.content.Context
import android.view.LayoutInflater
import androidx.leanback.widget.BaseCardView
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.LbCardSettingsBinding

class SettingsCardView(context: Context) : BaseCardView(
    context,
    null,
    R.style.FullCardStyle
) {
    var viewBinding = LbCardSettingsBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        isFocusable = true
    }
}