package ng.gov.imostate.database.converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import ng.gov.imostate.database.entity.DriverEntity
import ng.gov.imostate.database.entity.VehicleRouteEntity
import ng.gov.imostate.model.domain.VehicleRoute
import javax.inject.Inject

@ProvidedTypeConverter
class Converters @Inject constructor (
    private val moshi: Moshi
        ) {

    @TypeConverter
    fun fromDriver(value: DriverEntity?): String? {
        val adapter = moshi.adapter(DriverEntity::class.java)
        return adapter.toJson(value)
    }

    @TypeConverter
    fun toDriver(json: String?): DriverEntity? {
        val adapter = moshi.adapter(DriverEntity::class.java)
        return json?.let { adapter.fromJson(it) }
    }

    @TypeConverter
    fun fromListOfRoute(value: List<VehicleRouteEntity>?): String? {
        val routeListType = object : TypeToken<List<VehicleRouteEntity?>?>() {}.type
        val adapter: JsonAdapter<List<VehicleRouteEntity>>? = moshi.adapter(routeListType)
        return adapter?.toJson(value)
    }

    @TypeConverter
    fun toListOfRoute(json: String?): List<VehicleRouteEntity>? {
        val routeListType = object : TypeToken<List<VehicleRouteEntity?>?>() {}.type
        val adapter: JsonAdapter<List<VehicleRouteEntity>>? = moshi.adapter(routeListType)
        return if (json.isNullOrEmpty()) emptyList() else adapter?.fromJson(json)
    }
}
