package hibernate.v2.sunshine.ui.main.leanback

import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.Fragment
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.LbActivityMainBinding
import hibernate.v2.sunshine.ui.base.BaseActivity
import hibernate.v2.sunshine.ui.settings.SettingsFragment
import hibernate.v2.sunshine.ui.widget.SideMenuView
import hibernate.v2.sunshine.util.dpToPx

class MainActivity : BaseActivity<LbActivityMainBinding>(), SideMenuView.MenuItemClickListener {

    override fun getActivityViewBinding() = LbActivityMainBinding.inflate(layoutInflater)

    private var isMenuShown: Boolean = false
    private val maxExpandWidth = dpToPx(188)
    private val minExpandWidth = dpToPx(84)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setupAlarm()

        viewBinding.sideMenuView.setupDefaultMenu(SideMenuView.HOME_MENU)
        replaceSelectedFragment(MainFragment.getInstance(), SideMenuView.TAG_HOME)
        setupFocusListener()
    }

    override fun menuItemClick(menuId: Int) {
        if (viewBinding.sideMenuView.getCurrentSelected() == menuId) {
            return
        }
        val fragment: Fragment?
        val tag: String
        when (menuId) {
            SideMenuView.SETTING_MENU -> {
                viewBinding.sideMenuView.setCurrentSelected(SideMenuView.SETTING_MENU)
                fragment = SettingsFragment.getInstance()
                tag = SideMenuView.TAG_SETTINGS
            }
            else -> {
                viewBinding.sideMenuView.setCurrentSelected(SideMenuView.HOME_MENU)
                fragment = MainFragment.getInstance()
                tag = SideMenuView.TAG_HOME
            }
        }

        isMenuShown = false
        hideLeftMenu(viewBinding.sideMenuView)
        Handler(Looper.getMainLooper()).postDelayed({
            replaceSelectedFragment(fragment, tag)
        }, 100)
        Handler(Looper.getMainLooper()).postDelayed({
            setupFocusListener()
        }, 300)
    }

    private fun replaceSelectedFragment(fragment: Fragment?, tag: String) {
        viewBinding.mainFl.removeAllViewsInLayout()
        supportFragmentManager.beginTransaction()
            .replace(viewBinding.mainFl.id, fragment!!)
            .disallowAddToBackStack()
            .setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit)
            .commit()

        viewBinding.mainFl.requestFocus()
    }

    private fun setupFocusListener() {
        viewBinding.sideMenuView.onFocusChangeListener =
            View.OnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    viewBinding.sideMenuView.onFocusChangeListener = null
                    isMenuShown = true
                    showLeftMenu(view)
                } else {
                    isMenuShown = false
                    hideLeftMenu(view)
                }
            }
    }

    private fun showLeftMenu(view: View) {
        val width = viewBinding.sideMenuView.measuredWidth
        val valueAnimator = ValueAnimator.ofInt(width, maxExpandWidth)
        viewBinding.sideMenuView.setupMenuExpandedUI()

        viewBinding.transView.visibility = View.VISIBLE
        SideMenuView.animateView(view, valueAnimator)
    }

    private fun hideLeftMenu(view: View) {
        val width = viewBinding.sideMenuView.measuredWidth
        val valueAnimator = ValueAnimator.ofInt(width, minExpandWidth)
        viewBinding.sideMenuView.setupMenuClosedUI()

        viewBinding.transView.visibility = View.GONE
        SideMenuView.animateView(view, valueAnimator)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return if (isMenuShown) {
                resetLeftMenuUI()
            } else {
                onBackPressed()
                return false
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && isMenuShown) {
            return resetLeftMenuUI()
        }
        return false
    }

    private fun resetLeftMenuUI(): Boolean {
        isMenuShown = false
        viewBinding.mainFl.requestFocus()
        hideLeftMenu(viewBinding.sideMenuView)
        setupFocusListener()
        return true
    }

    override fun onDestroy() {
        viewBinding.sideMenuView.onFocusChangeListener = null
        super.onDestroy()
    }
}