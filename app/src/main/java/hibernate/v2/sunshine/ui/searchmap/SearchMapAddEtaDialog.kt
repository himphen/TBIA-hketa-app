package hibernate.v2.sunshine.ui.searchmap

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.model.Card

class SearchMapAddEtaDialog(
    private val card: Card.RouteStopAddCard,
    private val onClickListener: DialogInterface.OnClickListener,
    private val onCancelListener: DialogInterface.OnCancelListener? = null
) : DialogFragment() {

    override fun onCreateDialog(
        savedInstanceState: Bundle?
    ): Dialog {
        return MaterialAlertDialogBuilder(requireContext())
            .setMessage(
                getString(
                    R.string.dialog_add_eta_in_search_map_description,
                    card.route.routeNo,
                    card.stop.nameTc
                )
            )
            .setPositiveButton(R.string.dialog_add_eta_in_search_map_confirm_btn, onClickListener)
            .setNegativeButton(R.string.dialog_add_eta_in_search_map_cancel_btn, onClickListener)
            .setOnCancelListener(onCancelListener)
            .create()
    }
}