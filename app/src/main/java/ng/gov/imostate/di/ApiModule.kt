package ng.gov.imostate.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import ng.gov.imostate.api.ApiService
import ng.gov.imostate.model.domain.Api
import ng.gov.imostate.network.NetworkClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    fun providesApiService(
        networkClient: NetworkClient
    ): ApiService {
        return networkClient.getApiService(Api.STAGING)
    }

    @Singleton
    @Provides
    fun provideDispatcherIO(): CoroutineDispatcher {
        return Dispatchers.IO
    }
}