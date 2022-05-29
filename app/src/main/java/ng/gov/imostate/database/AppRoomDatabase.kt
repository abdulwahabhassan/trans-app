package ng.gov.imostate.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import ng.gov.imostate.database.converters.Converters
import ng.gov.imostate.database.dao.*
import ng.gov.imostate.database.entity.*
import ng.gov.imostate.util.Constants
import timber.log.Timber

@Database(
    entities = [
        DriverEntity::class,
        VehiclePreviousEntity::class,
        VehicleCurrentEntity::class,
        VehicleRouteEntity::class,
        TransactionEntity::class,
        RateEntity::class,
        AgentRouteEntity::class,
        UpdateEntity::class
               ],
    version = 1,
    exportSchema = false
)
abstract class AppRoomDatabase : RoomDatabase() {

    abstract fun vehicleLocalDao(): VehicleLocalDao
    abstract fun driverLocalDao(): DriverLocalDao
    abstract fun transactionLocalDao(): TransactionLocalDao
    abstract fun rateLocalDao(): RateLocalDao
    abstract fun routeLocalDao(): RouteLocalDao
    abstract fun agentRouteLocalDao(): AgentRouteLocalDao
    abstract fun updateLocalDao(): UpdateLocalDao

    companion object {

        @Volatile private var instance: AppRoomDatabase? = null

        fun getInstance(context: Context): AppRoomDatabase {
            Timber.d("database get instance")
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(appContext: Context): AppRoomDatabase {
            return Room.databaseBuilder(
                appContext,
                AppRoomDatabase::class.java,
                Constants.DATABASE_NAME
            ).addTypeConverter(Converters(
                Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .build())
            )
                .build()
        }

    }
}