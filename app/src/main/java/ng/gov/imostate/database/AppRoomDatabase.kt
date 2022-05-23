package ng.gov.imostate.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import ng.gov.imostate.database.converters.Converters
import ng.gov.imostate.database.dao.*
import ng.gov.imostate.database.entity.*
import ng.gov.imostate.worker.DatabaseWorker
import ng.gov.imostate.util.Constants
import timber.log.Timber

@Database(
    entities = [
        DriverEntity::class,
        VehicleEntity::class,
        RouteEntity::class,
        TransactionEntity::class,
        RateEntity::class
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
            )
//                .addCallback(
//                    object : RoomDatabase.Callback() {
//                        override fun onCreate(db: SupportSQLiteDatabase) {
//                            super.onCreate(db)
//                            val request = OneTimeWorkRequestBuilder<DatabaseWorker>()
//                                .setInputData(
//                                    workDataOf(
////                                        DatabaseWorker.VEHICLE_KEY_FILENAME to Constants.VEHICLE_DATA_FILENAME,
////                                        DatabaseWorker.DRIVER_KEY_FILENAME to Constants.DRIVER_DATA_FILENAME
//                                        DatabaseWorker.VEHICLE_DRIVER_KEY_FILENAME to Constants.VEHICLE_DRIVER_DATA_FILENAME
//                                    )
//                                )
//                                .build()
//                            WorkManager.getInstance(appContext).enqueue(request)
//                        }
//                    }
//                )
                .addTypeConverter(Converters(
                Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .build())
            )
                .build()
        }

    }
}