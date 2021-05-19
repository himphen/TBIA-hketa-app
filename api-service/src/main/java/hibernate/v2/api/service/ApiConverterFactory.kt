package hibernate.v2.api.service

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.RequestBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class ApiConverterFactory(private val gson: Gson) : Converter.Factory() {

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<Annotation>,
        methodAnnotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody> {
        val adapter = gson.getAdapter(TypeToken.get(type))
        return RequestConverter(gson, adapter)
    }
}
