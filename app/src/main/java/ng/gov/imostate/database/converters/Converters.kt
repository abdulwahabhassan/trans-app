package ng.gov.imostate.database.converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import ng.gov.imostate.database.entity.DriverEntity
import ng.gov.imostate.database.entity.RouteEntity
import ng.gov.imostate.model.domain.Driver
import ng.gov.imostate.model.domain.Route
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

//    @TypeConverter
//    fun fromRoute(value: Route?): String? {
//        val adapter = moshi.adapter(Route::class.java)
//        return adapter.toJson(value)
//    }
//
//    @TypeConverter
//    fun toRoute(json: String?): Route? {
//        val adapter = moshi.adapter(Route::class.java)
//        return json?.let { adapter.fromJson(it) }
//    }

    @TypeConverter
    fun fromListOfRoute(value: List<RouteEntity>?): String? {
        val routeListType = object : TypeToken<List<Route?>?>() {}.type
        val adapter: JsonAdapter<List<RouteEntity>>? = moshi.adapter(routeListType)
        return adapter?.toJson(value)
    }

    @TypeConverter
    fun toListOfRoute(json: String?): List<RouteEntity>? {
        val routeListType = object : TypeToken<List<RouteEntity?>?>() {}.type
        val adapter: JsonAdapter<List<RouteEntity>>? = moshi.adapter(routeListType)
        return if (json.isNullOrEmpty()) emptyList() else adapter?.fromJson(json)
    }
}
