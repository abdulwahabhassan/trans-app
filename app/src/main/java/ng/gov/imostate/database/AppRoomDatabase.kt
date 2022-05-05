package ng.gov.imostate.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import ng.gov.imostate.database.dao.DriverLocalDao
import ng.gov.imostate.database.dao.VehicleLocalDao
import ng.gov.imostate.worker.DatabaseWorker
import ng.gov.imostate.database.entity.DriverEntity
import ng.gov.imostate.database.entity.RouteEntity
import ng.gov.imostate.database.entity.UserEntity
import ng.gov.imostate.database.entity.VehicleEntity
import ng.gov.imostate.util.Constants
import timber.log.Timber

@Database(
    entities = [UserEntity::class, DriverEntity::class, VehicleEntity::class, RouteEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppRoomDatabase : RoomDatabase() {

    abstract fun vehicleLocalDao(): VehicleLocalDao
    abstract fun driverLocalDao(): DriverLocalDao

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
            ).addCallback(
                    object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            val request = OneTimeWorkRequestBuilder<DatabaseWorker>()
                                .setInputData(
                                    workDataOf(
                                        DatabaseWorker.VEHICLE_KEY_FILENAME to Constants.VEHICLE_DATA_FILENAME,
                                        DatabaseWorker.DRIVER_KEY_FILENAME to Constants.DRIVER_DATA_FILENAME
                                    )
                                )
                                .build()
                            WorkManager.getInstance(appContext).enqueue(request)
                        }
                    }
            ).build()
        }

    }
}