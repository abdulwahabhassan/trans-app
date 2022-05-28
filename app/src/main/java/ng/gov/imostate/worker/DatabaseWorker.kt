package ng.gov.imostate.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import ng.gov.imostate.database.AppRoomDatabase
import ng.gov.imostate.database.entity.VehiclePreviousEntity
import timber.log.Timber

class DatabaseWorker (
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val vehicleFileName = inputData.getString(VEHICLE_KEY_FILENAME)
            val driverFileName = inputData.getString(DRIVER_KEY_FILENAME)
            val vehicleDriverFileName = inputData.getString(VEHICLE_DRIVER_KEY_FILENAME)

            Timber.d("work started")

//            if (vehicleFileName != null) {
//                applicationContext.assets.open(vehicleFileName).use { inputStream ->
//                    JsonReader(inputStream.reader()).use { jsonReader ->
//                        val vehicleListType = object : TypeToken<List<VehicleEntity>>() {}.type
//                        val vehicles: List<VehicleEntity> = Gson().fromJson(jsonReader, vehicleListType)
//                        Timber.d("vehicles $vehicles")
//                        val database = AppRoomDatabase.getInstance(applicationContext)
//                        database.vehicleLocalDao().insertAllVehicles(vehicles)
//                        Timber.d("Success database - all vehicles set in database")
//                        Result.success()
//                    }
//                }
//            } else {
//                Timber.d("Error database - no valid vehicles filename")
//                Result.failure()
//            }
//
//            if (driverFileName != null) {
//                applicationContext.assets.open(driverFileName).use { inputStream ->
//                    JsonReader(inputStream.reader()).use { jsonReader ->
//                        val driverListType = object : TypeToken<List<DriverEntity>>() {}.type
//                        val drivers: List<DriverEntity> = Gson().fromJson(jsonReader, driverListType)
//                        Timber.d("drivers $drivers")
//                        val database = AppRoomDatabase.getInstance(applicationContext)
//                        database.driverLocalDao().insertAllDrivers(drivers)
//                        Timber.d("Success database - all drivers set in database")
//                        Result.success()
//                    }
//                }
//            } else {
//                Timber.d("Error database - no valid driver filename")
//                Result.failure()
//            }

            if (vehicleDriverFileName != null) {
                applicationContext.assets.open(vehicleDriverFileName).use { inputStream ->
                    JsonReader(inputStream.reader()).use { jsonReader ->
                        val vehicleListType = object : TypeToken<List<VehiclePreviousEntity>>() {}.type
                        val vehiclePrevious: List<VehiclePreviousEntity> = Gson().fromJson(jsonReader, vehicleListType)
                        Timber.d("vehicles and drivers $vehiclePrevious")
                        val database = AppRoomDatabase.getInstance(applicationContext)
                        database.vehicleLocalDao().insertAllVehiclesFromPreviousEnumeration(vehiclePrevious)
                        Timber.d("Success database - all vehicles and drivers set in database")
                        Result.success()
                    }
                }
            } else {
                Timber.d("Error database - no valid vehicle-driver filename")
                Result.failure()
            }

        } catch (ex: Exception) {
            Timber.d("Error database $ex")
            Result.failure()
        }
    }

    companion object {
        const val VEHICLE_KEY_FILENAME = "VEHICLE_DATA_FILENAME"
        const val DRIVER_KEY_FILENAME = "DRIVER_DATA_FILENAME"
        const val VEHICLE_DRIVER_KEY_FILENAME = "VEHICLE_DRIVER_DATA_FILENAME"
    }
}