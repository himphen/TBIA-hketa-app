package hibernate.v2.sunshine.ui.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding
import hibernate.v2.sunshine.R

abstract class BaseActivity<T : ViewBinding> : FragmentActivity() {
    lateinit var viewBinding: T

    abstract fun getActivityViewBinding(): T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = getActivityViewBinding()
        setContentView(viewBinding.root)
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