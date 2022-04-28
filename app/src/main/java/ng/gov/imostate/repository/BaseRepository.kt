package ng.gov.imostate.repository

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ng.gov.imostate.model.response.ApiErrorResponse
import ng.gov.imostate.model.result.ApiResult
import ng.gov.imostate.util.NetworkConnectivityUtil
import retrofit2.HttpException
import timber.log.Timber

@Suppress("UNCHECKED_CAST")
open class BaseRepository {

    suspend fun <T> coroutineHandler(
        context: Context,
        dispatcher: CoroutineDispatcher,
        networkConnectivityUtil: NetworkConnectivityUtil,
        apiRequest: suspend () -> T
    ): ApiResult<T> {
        return withContext(dispatcher) {
            if (networkConnectivityUtil.networkConnectivityStatus.value == false) {
                ApiResult.Error("Check your internet connection!")
            } else {
                try {
                    ApiResult.Success(apiRequest.invoke())
                } catch (e: HttpException) {
                    val apiError = throwableResponse(e)
                    Timber.d("Http Error Http Status Code - ${e.code()} " +
                            ": Api Message - ${apiError?.message} " +
                            ": Api Errors - ${apiError?.errors} " +
                            ": Api Error Code - ${apiError?.errorCode}")
                    ApiResult.Error(apiError?.errors?.toString())
                } catch (e: Exception) {
                    Timber.d("Exception Error ${e.message}")
                    ApiResult.Error(e.localizedMessage)
                }
            }
        }
    }

    open val dispatcher = Dispatchers.IO

    private fun throwableResponse(e: HttpException): ApiErrorResponse? {
        return try {
            e.response()?.errorBody()?.source()?.let {
                val moshiAdapter = Moshi.Builder()
                    .addLast(KotlinJsonAdapterFactory())
                    .build()
                    .adapter(ApiErrorResponse::class.java)
                moshiAdapter.fromJson(it)
            }
        } catch (t: Throwable) {
            Timber.d("Error $t")
            null
        }
    }
}
