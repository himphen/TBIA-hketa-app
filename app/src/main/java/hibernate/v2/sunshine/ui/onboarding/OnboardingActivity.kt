package hibernate.v2.sunshine.ui.onboarding

import android.os.Bundle
import hibernate.v2.sunshine.R
import hibernate.v2.sunshine.databinding.ActivityOnboardingBinding
import hibernate.v2.sunshine.ui.base.BaseActivity

class OnboardingActivity : BaseActivity<ActivityOnboardingBinding>() {

    override fun getActivityViewBinding() = ActivityOnboardingBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager.beginTransaction()
            .replace(viewBinding.onboardingFl.id, OnboardingFragment.getInstance())
            .disallowAddToBackStack()
            .setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit)
            .commit()
    }
}