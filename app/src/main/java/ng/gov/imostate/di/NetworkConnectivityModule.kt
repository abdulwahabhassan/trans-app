package ng.gov.imostate.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ng.gov.imostate.util.NetworkConnectivityUtil
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkConnectivityModule {

    @Provides
    @Singleton
    fun providesNetworkConnectivityUtil(
        @ApplicationContext appContext: Context
    ): NetworkConnectivityUtil {
        return NetworkConnectivityUtil.instance!!

    }
}