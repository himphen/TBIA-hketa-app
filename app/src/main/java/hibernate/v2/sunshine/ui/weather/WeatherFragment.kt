package hibernate.v2.sunshine.ui.weather

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import coil.load
import hibernate.v2.api.model.openweather.OneCall
import hibernate.v2.sunshine.databinding.FragmentWeatherBinding
import hibernate.v2.sunshine.ui.base.BaseFragment
import hibernate.v2.sunshine.ui.traffic.TrafficFragment
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import kotlin.math.roundToInt

class WeatherFragment : BaseFragment<FragmentWeatherBinding>() {

    companion object {
        fun getInstance() = WeatherFragment()
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentWeatherBinding.inflate(inflater, container, false)

    private val viewModel by inject<WeatherViewModel>()

    init {
        lifecycleScope.launchWhenCreated {
            launch {
                viewModel.oneCallData.observe(this@WeatherFragment, {
                    processData(it)
                })
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun processData(oneCall: OneCall) {
        viewBinding?.humidityTv?.text =
            (oneCall.current?.humidity?.toString() ?: "-") + "%"

        viewBinding?.feelLikeTempTv?.text =
            (oneCall.current?.feelsLike?.roundToInt()?.toString() ?: "-") + "°C"

        viewBinding?.currentTempTv?.text =
            oneCall.current?.temp?.roundToInt()?.toString() ?: "-"

        viewBinding?.maxTempTv?.text =
            oneCall.daily?.firstOrNull()?.temp?.max?.roundToInt()?.toString() ?: "-"

        viewBinding?.minTempTv?.text =
            oneCall.daily?.firstOrNull()?.temp?.min?.roundToInt()?.toString() ?: "-"

        viewBinding?.popTv?.text =
            oneCall.daily?.firstOrNull()?.pop?.let {
                (it * 100).roundToInt().toString() + "%"
            } ?: run {
                "-"
            }

        val resName = oneCall.current?.weather?.firstOrNull()?.icon?.let {
            "weather_icon_$it"
        } ?: run {
            "weather_icon_01d"
        }

        viewBinding?.weatherIconIv?.load(
            resources.getIdentifier(resName, "drawable", activity?.packageName)
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
    }

    private fun initData() {
        viewModel.getWeatherInfo()

        viewBinding?.backdropIv?.load(
           "https://source.unsplash.com/1920x1080/?nature,water"
        )
    }
}