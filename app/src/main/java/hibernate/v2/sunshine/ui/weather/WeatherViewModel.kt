package hibernate.v2.sunshine.ui.weather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.himphen.logger.Logger
import hibernate.v2.api.model.openweather.OneCall
import hibernate.v2.sunshine.repository.WeatherRepository
import hibernate.v2.sunshine.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WeatherViewModel(private val repo: WeatherRepository) : BaseViewModel() {
    val oneCallData = MutableLiveData<OneCall>()

    fun getWeatherInfo() {
        Logger.t("lifecycle").d("getWeatherInfo")
        viewModelScope.launch {
            val database =
                Firebase.database("https://android-tv-c733a-default-rtdb.asia-southeast1.firebasedatabase.app/")
            val weatherRef = database.reference.child("weather")

            weatherRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.getValue<OneCall>()?.let {
                        oneCallData.postValue(it)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Logger.e(error.toException(), "Failed to read value.")
                }
            })
        }
    }

    fun getWeatherInfoFromApi() {
        Logger.t("lifecycle").d("getWeatherInfo")
        viewModelScope.launch {
            try {
                launch {
                    oneCallData.postValue(repo.currentWeather(22.32215, 114.16994))
                }
            } catch (e: Exception) {
                Logger.e(e, "net error")
                withContext(Dispatchers.Main) {
                }
            }
        }
    }
}
