package hibernate.v2.tbia.ui.onboarding.mobile

import android.os.Bundle
import hibernate.v2.tbia.R
import hibernate.v2.tbia.databinding.ActivityOnboardingBinding
import hibernate.v2.tbia.ui.base.BaseFragmentActivity

class OnboardingActivity : BaseFragmentActivity<ActivityOnboardingBinding>() {

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
