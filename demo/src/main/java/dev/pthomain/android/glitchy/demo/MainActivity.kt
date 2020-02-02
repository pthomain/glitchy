package dev.pthomain.android.glitchy.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.Gson
import dev.pthomain.android.glitchy.Glitchy
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val glitchCallAdapterFactory = Glitchy.createCallAdapterFactory()
        val apiErrorCallAdapterFactory = Glitchy.createCallAdapterFactory(ApiError.Factory())

        val glitchRetrofit = getRetrofit(glitchCallAdapterFactory)
        val apiErrorRetrofit = getRetrofit(apiErrorCallAdapterFactory)
    }

    private fun getRetrofit(callAdapterFactory: CallAdapter.Factory) =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .addCallAdapterFactory(callAdapterFactory)
            .build()

    companion object {
        internal const val BASE_URL = "https://catfact.ninja/"
        internal const val ENDPOINT = "fact"
    }
}
