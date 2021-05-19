package hibernate.v2.api.service

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import java.io.OutputStreamWriter
import java.nio.charset.Charset
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Buffer
import org.json.JSONObject
import retrofit2.Converter

class RequestConverter<T>(
    private val gson: Gson,
    private val adapter: TypeAdapter<T>
) : Converter<T, RequestBody> {

    companion object {
        private val MEDIA_TYPE = "application/json; charset=utf-8".toMediaTypeOrNull()
        private val UTF_8 = Charset.forName("UTF-8")
    }

    override fun convert(value: T): RequestBody {
        val buffer = Buffer()
        val writer = OutputStreamWriter(
            buffer.outputStream(),
            UTF_8
        )
        val jsonWriter = gson.newJsonWriter(writer)
        adapter.write(jsonWriter, value)
        jsonWriter.close()
        val mainObject = JSONObject(buffer.readByteString().utf8())
        return mainObject.toString().toRequestBody(MEDIA_TYPE)
    }
}
