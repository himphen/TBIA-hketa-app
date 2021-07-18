package hibernate.v2.sunshine.ui.base

import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.ActivityContainerBinding

abstract class BaseActivity<T : ViewBinding> : AppCompatActivity() {
    lateinit var viewBinding: T

    abstract fun getActivityViewBinding(): T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = getActivityViewBinding()
        setContentView(viewBinding.root)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    protected fun initActionBar(
        toolbar: Toolbar?,
        titleString: String? = null, subtitleString: String? = null,
        @StringRes titleId: Int? = null, @StringRes subtitleId: Int? = null
    ) {
        toolbar?.let {
            setSupportActionBar(toolbar)
            supportActionBar?.let { ab ->
                ab.setDisplayHomeAsUpEnabled(true)
                ab.setHomeButtonEnabled(true)
                titleString?.let {
                    ab.title = titleString
                }
                titleId?.let {
                    ab.setTitle(titleId)
                }
                subtitleString?.let {
                    ab.subtitle = subtitleString
                }
                subtitleId?.let {
                    ab.setSubtitle(subtitleId)
                }
            }
        }
    }

    fun initFragment(fragment: Fragment?, titleString: String?, titleId: Int?) {
        fragment?.let {
            setContentView(viewBinding.root)
            initActionBar(
                (viewBinding as? ActivityContainerBinding)?.toolbar?.root,
                titleString = titleString, titleId = titleId
            )

            supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
        }
    }

    fun popBackStack(): Boolean {
        return if (supportFragmentManager.backStackEntryCount == 0) {
            false
        } else {
            supportFragmentManager.popBackStack()
            true
        }
    }
}