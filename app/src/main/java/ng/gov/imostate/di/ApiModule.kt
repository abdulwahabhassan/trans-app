package ng.gov.imostate.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ng.gov.imostate.api.ApiService
import ng.gov.imostate.model.Api
import ng.gov.imostate.network.NetworkClient

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    fun providesApiService(
        networkClient: NetworkClient
    ): ApiService {
        return networkClient.getApiService(Api.STAGING)
    }

}