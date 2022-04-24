package ng.gov.imostate.ui

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ng.gov.imostate.model.AppModule
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject


class AppConfigRepository @Inject constructor (private val dataStore: DataStore<Preferences>) {

    val appConfigPreferencesFlow: Flow<AppConfigPreferences> = dataStore.data.catch { exception ->
            //throw an IOException when an error is encountered while reading data
            if (exception is IOException) {
                Timber.d(exception, "Error reading preferences.")
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            mapUserPreferences(preferences)
        }

    suspend fun fetchInitialPreferences() =
        mapUserPreferences(dataStore.data.first().toPreferences())

    private fun mapUserPreferences(preferences: Preferences): AppConfigPreferences {
        return AppConfigPreferences(
            appName = preferences[PreferencesKeys.APP_NAME]
            )
    }

    suspend fun updateAppConfig(appConfig: AppModule) {
        dataStore.edit { preferences ->
           with(appConfig) {
               preferences[PreferencesKeys.APP_NAME] = appName!!
           }
        }
    }

    data class AppConfigPreferences(
        val appName: String?,
    )

    //define preferences keys
    private object PreferencesKeys {
        val APP_NAME = stringPreferencesKey("appName")
    }
}