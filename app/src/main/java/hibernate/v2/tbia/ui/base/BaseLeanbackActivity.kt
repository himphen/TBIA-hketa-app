package hibernate.v2.tbia.ui.base

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding
import hibernate.v2.tbia.R
import hibernate.v2.tbia.util.GeneralUtils

abstract class BaseLeanbackActivity<T : ViewBinding> : FragmentActivity() {
    lateinit var viewBinding: T

    open var fragment: Fragment? = null
    open var titleId: Int? = null
    open var titleString: String? = null

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(GeneralUtils.initLanguage(newBase))
    }

    abstract fun getActivityViewBinding(): T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = getActivityViewBinding()
        setContentView(viewBinding.root)
        initFragment(fragment)
    }

    fun initFragment(fragment: Fragment?) {
        fragment?.let {
            setContentView(viewBinding.root)

            supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
        }
    }
}
