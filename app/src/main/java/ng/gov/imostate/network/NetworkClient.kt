package ng.gov.imostate.network

import ng.gov.imostate.model.domain.Api
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NetworkClient @Inject constructor (
    private val moshiConverterFactory: MoshiConverterFactory,
) {

    private val loggerInterceptor = HttpLoggingInterceptor().apply {
        this.level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient by lazy {
        OkHttpClient.Builder().apply {
            this
                .addInterceptor(loggerInterceptor)
                .addInterceptor(Interceptor { chain: Interceptor.Chain ->
                val request = chain
                    .request()
                    .newBuilder()
                    .addHeader("Connection", "close")
                    .build()
                    chain.proceed(request)
                })
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
        }.build()
    }

    inline fun <reified T>getApiService(api: Api): T {
        return when (api) {
            Api.STAGING -> { getRetrofitInstance(api).create(T::class.java) }
            Api.PROD -> { getRetrofitInstance(api).create(T::class.java) }
        }
    }

    fun getRetrofitInstance(
        api: Api
    ): Retrofit {
        return Retrofit.Builder()
             .baseUrl(api.baseUrl)
            .client(okHttpClient)
            .addConverterFactory(moshiConverterFactory)
            .build()
    }

}