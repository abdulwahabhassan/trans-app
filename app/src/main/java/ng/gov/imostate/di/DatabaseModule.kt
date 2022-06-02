package ng.gov.imostate.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ng.gov.imostate.database.AppRoomDatabase
import ng.gov.imostate.database.dao.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideVehicleLocalDAO(
        @ApplicationContext appContext: Context
    ): VehicleLocalDao {
        return AppRoomDatabase.getInstance(appContext).vehicleLocalDao()
    }

    @Provides
    @Singleton
    fun provideDriverLocalDAO(
        @ApplicationContext appContext: Context
    ): DriverLocalDao {
        return AppRoomDatabase.getInstance(appContext).driverLocalDao()
    }

    @Provides
    @Singleton
    fun provideTransactionLocalDAO(
        @ApplicationContext appContext: Context
    ): TransactionLocalDao {
        return AppRoomDatabase.getInstance(appContext).transactionLocalDao()
    }

    @Provides
    @Singleton
    fun provideRateLocalDAO(
        @ApplicationContext appContext: Context
    ): RateLocalDao {
        return AppRoomDatabase.getInstance(appContext).rateLocalDao()
    }

    @Provides
    @Singleton
    fun provideRouteLocalDAO(
        @ApplicationContext appContext: Context
    ): RouteLocalDao {
        return AppRoomDatabase.getInstance(appContext).routeLocalDao()
    }

    @Provides
    @Singleton
    fun provideAgentRouteLocalDAO(
        @ApplicationContext appContext: Context
    ): AgentRouteLocalDao {
        return AppRoomDatabase.getInstance(appContext).agentRouteLocalDao()
    }

    @Provides
    @Singleton
    fun provideUpdateLocalDAO(
        @ApplicationContext appContext: Context
    ): UpdateLocalDao {
        return AppRoomDatabase.getInstance(appContext).updateLocalDao()
    }
}