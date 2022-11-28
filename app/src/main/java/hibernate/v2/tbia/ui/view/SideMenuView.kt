package hibernate.v2.tbia.ui.view

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import hibernate.v2.tbia.R
import hibernate.v2.tbia.databinding.SideMenuLayoutBinding

class SideMenuView : LinearLayout {
    companion object {
        const val HOME_MENU = 11
        const val SETTING_MENU = 12

        const val TAG_HOME = "HOME"
        const val TAG_SETTINGS = "SETTINGS"

        fun animateView(view: View, valueAnimator: ValueAnimator) {
            valueAnimator.addUpdateListener { animation ->
                view.layoutParams.width = animation.animatedValue as Int
                view.requestLayout()
            }

            valueAnimator.interpolator = AccelerateInterpolator()
            valueAnimator.duration = 100
            valueAnimator.start()
        }
    }

    private var menuItemClick: MenuItemClickListener? = null
    private var menuTitles = emptyArray<String>()
    private var currentSelected: Int = HOME_MENU
    private var leftMenusShown: Boolean = false

    var viewBinding: SideMenuLayoutBinding? = null

    interface MenuItemClickListener {
        fun menuItemClick(menuId: Int)
    }

    constructor(context: Context) : super(context) {
        setupUI(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setupUI(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        setupUI(context)
    }

    private fun setupUI(context: Context) {
        menuItemClick = context as MenuItemClickListener
        menuTitles = resources.getStringArray(R.array.leanback_main_tab_title)
        orientation = VERTICAL
        gravity = Gravity.CENTER
        setBackgroundColor(ContextCompat.getColor(context, R.color.lb_menu_bg))
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        viewBinding = SideMenuLayoutBinding.inflate(inflater, this)
        viewBinding?.cllHomeView?.setOnClickListener {
            menuItemClick!!.menuItemClick(HOME_MENU)
        }
        viewBinding?.cllSettingsView?.setOnClickListener {
            menuItemClick!!.menuItemClick(SETTING_MENU)
        }
    }

    private fun focusCurrentSelectedMenu() {
        when (currentSelected) {
            HOME_MENU -> {
                viewBinding?.cllHomeView?.apply {
                    requestFocus()
                }
            }
            SETTING_MENU -> {
                viewBinding?.cllSettingsView?.apply {
                    requestFocus()
                }
            }
        }
    }

    fun setCurrentSelected(currentSelected: Int) {
        this.currentSelected = currentSelected
    }

    fun getCurrentSelected(): Int {
        return currentSelected
    }

    private fun highlightCurrentSelectedMenu() {
        when (currentSelected) {
            HOME_MENU -> {
                viewBinding?.cllHomeView?.setBackgroundColor(
                    ContextCompat.getColor(
                        this.context,
                        R.color.lb_menu_bg_selected
                    )
                )
                viewBinding?.civHome?.imageTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            this.context,
                            R.color.lb_menu_icon_selected
                        )
                    )
            }
            SETTING_MENU -> {
                viewBinding?.cllSettingsView?.setBackgroundColor(
                    ContextCompat.getColor(
                        this.context,
                        R.color.lb_menu_bg_selected
                    )
                )
                viewBinding?.civSettings?.imageTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            this.context,
                            R.color.lb_menu_icon_selected
                        )
                    )
            }
        }
    }

    private fun resetMenusText() {
        viewBinding?.txtHomeMenuLabel?.apply {
            text = ""
        }
        viewBinding?.txtSettingsMenuLabel?.apply {
            text = ""
        }
    }

    private fun setMenusText() {
        viewBinding?.txtHomeMenuLabel?.apply {
            text = menuTitles[0]
        }
        viewBinding?.txtSettingsMenuLabel?.apply {
            text = menuTitles[1]
        }
    }

    fun setupMenuExpandedUI() {
        Handler(Looper.getMainLooper()).postDelayed({
            setMenusText()
            changeMenuFocusStatus(true)
            focusCurrentSelectedMenu()
        }, 100)
    }

    fun setupMenuClosedUI() {
        resetMenusText()
        changeMenuFocusStatus(false)
    }

    private fun changeMenuFocusStatus(status: Boolean) {
        val count = childCount
        for (i in 0 until count) {
            val childView = getChildAt(i)
            childView.apply {
                isFocusable = status
                isFocusableInTouchMode = status
                if (!status) {
                    clearFocus()
                    highlightCurrentSelectedMenu()
                } else {
                    setBackgroundColor(0)
                    background =
                        ContextCompat.getDrawable(this.context, R.drawable.drawable_menu_hover)
                    changeMenuColor()
                }
            }
        }
    }

    private fun changeMenuColor() {
        when (currentSelected) {
            HOME_MENU -> {
                viewBinding?.civHome?.imageTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        this.context,
                        R.color.white
                    )
                )
            }
            SETTING_MENU -> {
                viewBinding?.civSettings?.imageTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(
                            this.context,
                            R.color.white
                        )
                    )
            }
        }
    }

    fun setupDefaultMenu(menuId: Int) {
        currentSelected = menuId
        leftMenusShown = false
        changeMenuFocusStatus(false)
    }

    fun changeFocusTo(menuId: Int) {
        resetMenus()
        currentSelected = menuId
        highlightCurrentSelectedMenu()
    }

    private fun resetMenus() {
        val color = ColorStateList.valueOf(
            ContextCompat.getColor(
                this.context,
                R.color.white
            )
        )
        viewBinding?.civHome?.imageTintList = color
        viewBinding?.cllHomeView?.setBackgroundColor(0)

        viewBinding?.civSettings?.imageTintList = color
        viewBinding?.cllSettingsView?.setBackgroundColor(0)
    }
}
