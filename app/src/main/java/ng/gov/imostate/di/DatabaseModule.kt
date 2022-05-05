package ng.gov.imostate.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ng.gov.imostate.database.AppRoomDatabase
import ng.gov.imostate.database.dao.DriverLocalDao
import ng.gov.imostate.database.dao.VehicleLocalDao
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
}