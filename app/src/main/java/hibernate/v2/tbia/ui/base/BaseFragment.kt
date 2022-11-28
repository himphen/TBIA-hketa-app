package hibernate.v2.tbia.ui.base

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import hibernate.v2.tbia.R

abstract class BaseFragment<T : ViewBinding> : Fragment() {
    var viewBinding: T? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = getViewBinding(inflater, container, savedInstanceState)
        return viewBinding?.root
    }

    abstract fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): T?

    fun locationPermissionRequest(callback: () -> Unit) = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val context = context ?: return@registerForActivityResult
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                callback()
            }
            else -> {
                MaterialAlertDialogBuilder(context)
                    .setTitle(R.string.dialog_location_permission_title)
                    .setMessage(R.string.dialog_location_permission_message)
                    .setPositiveButton(R.string.dialog_location_permission_pos_btn) { dialog, _ ->
                        startActivity(
                            Intent().apply {
                                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                data = Uri.fromParts("package", activity?.packageName, null)
                            }
                        )
                    }
                    .setNegativeButton(R.string.dialog_cancel_btn) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
    }
}
