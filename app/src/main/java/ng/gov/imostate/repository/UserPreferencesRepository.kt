package ng.gov.imostate.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject


class UserPreferencesRepository @Inject constructor (private val dataStore: DataStore<Preferences>) {

    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data.catch { exception ->
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

    private fun mapUserPreferences(preferences: Preferences): UserPreferences {
        return UserPreferences(
            token = preferences[PreferencesKeys.TOKEN],
            loggedIn = preferences[PreferencesKeys.LOGGED_IN],
            agentName = preferences[PreferencesKeys.AGENT_NAME],
            agentId = preferences[PreferencesKeys.AGENT_ID],
            photo = preferences[PreferencesKeys.PHOTO],
            agentFirstName = preferences[PreferencesKeys.AGENT_FIRST_NAME],
            agentMiddleName = preferences[PreferencesKeys.AGENT_MIDDLE_NAME],
            agentLastName = preferences[PreferencesKeys.AGENT_LAST_NAME],
            type = preferences[PreferencesKeys.TYPE],
            phone = preferences[PreferencesKeys.PHONE],
            address = preferences[PreferencesKeys.ADDRESS],
            onboardingDate = preferences[PreferencesKeys.ONBOARDING_DATE],
            email = preferences[PreferencesKeys.EMAIL],
            emailVerifiedAt = preferences[PreferencesKeys.EMAIL_VERIFIED_AT],
            status = preferences[PreferencesKeys.STATUS],
            createdBy = preferences[PreferencesKeys.CREATED_BY],
            createdAt = preferences[PreferencesKeys.CREATED_AT],
            updatedAt = preferences[PreferencesKeys.UPDATED_AT],
            bvn = preferences[PreferencesKeys.BVN],
            lastSyncTime = preferences[PreferencesKeys.LAST_SYNC_TIME],
            currentWalletBalance = preferences[PreferencesKeys.CURRENT_WALLET_BALANCE] ?: 0.00
        )
    }

    suspend fun updateCurrentWalletBalance(balance: Double) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.CURRENT_WALLET_BALANCE] = balance
        }
    }

    suspend fun updateLoginStatus(loggedIn: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.LOGGED_IN] = loggedIn
        }
    }

    suspend fun updateLastSyncTime(time: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_SYNC_TIME] = time
        }
    }

    suspend fun updateUserPreferences(userPreferences: UserPreferences) {
        dataStore.edit { preferences ->
           with(userPreferences) {
               preferences[PreferencesKeys.TOKEN] = token!!
               preferences[PreferencesKeys.AGENT_NAME] = agentName!!
               preferences[PreferencesKeys.AGENT_ID] = agentId!!
               preferences[PreferencesKeys.PHOTO] = photo!!
               preferences[PreferencesKeys.AGENT_FIRST_NAME] = agentFirstName!!
               preferences[PreferencesKeys.AGENT_MIDDLE_NAME] = agentMiddleName!!
               preferences[PreferencesKeys.AGENT_LAST_NAME] = agentLastName!!
               preferences[PreferencesKeys.TYPE] = type!!
               preferences[PreferencesKeys.PHONE] = phone!!
               preferences[PreferencesKeys.ADDRESS] = address!!
               preferences[PreferencesKeys.ONBOARDING_DATE] = onboardingDate!!
               preferences[PreferencesKeys.EMAIL] = email!!
               preferences[PreferencesKeys.EMAIL_VERIFIED_AT] = emailVerifiedAt!!
               preferences[PreferencesKeys.STATUS] = status!!
               preferences[PreferencesKeys.CREATED_BY] = createdBy!!
               preferences[PreferencesKeys.CREATED_AT] = createdAt!!
               preferences[PreferencesKeys.UPDATED_AT] = updatedAt!!
               preferences[PreferencesKeys.BVN] = bvn!!
               preferences[PreferencesKeys.CURRENT_WALLET_BALANCE] = currentWalletBalance
           }
        }
    }

    data class UserPreferences(
        val token: String? = null,
        val loggedIn: Boolean? = false,
        val agentName: String? = null,
        val agentId: Long? = null,
        val photo: String? = null,
        val agentFirstName: String? = null,
        val agentMiddleName: String? = null,
        val agentLastName: String? = null,
        val type: String? = null,
        val phone: String? = null,
        val address: String? = null,
        val onboardingDate: String? = null,
        val email:String? = null,
        val emailVerifiedAt: String? = null,
        val status: String? = null,
        val createdBy: Long? = null,
        val createdAt: String? = null,
        val updatedAt: String? = null,
        val bvn: String? = null,
        val lastSyncTime: String? = null,
        val currentWalletBalance: Double = 0.00
    )

    //define preferences keys
    private object PreferencesKeys {
        val TOKEN = stringPreferencesKey("token")
        val LOGGED_IN = booleanPreferencesKey("loggedIn")
        val AGENT_NAME = stringPreferencesKey("agentName")
        val AGENT_ID = longPreferencesKey("agentId")
        val PHOTO = stringPreferencesKey("businessName")
        val AGENT_FIRST_NAME = stringPreferencesKey("agentFirstName")
        val AGENT_MIDDLE_NAME = stringPreferencesKey("agentMiddleName")
        val AGENT_LAST_NAME = stringPreferencesKey("agentLastName")
        val TYPE = stringPreferencesKey("userType")
        val PHONE = stringPreferencesKey("phone")
        val ADDRESS = stringPreferencesKey("address")
        val ONBOARDING_DATE = stringPreferencesKey("onboardingDate")
        val EMAIL = stringPreferencesKey("email")
        val EMAIL_VERIFIED_AT = stringPreferencesKey("emailVerifiedAt")
        val STATUS = stringPreferencesKey("status")
        val CREATED_BY = longPreferencesKey("createdBy")
        val CREATED_AT = stringPreferencesKey("createdAt")
        val UPDATED_AT = stringPreferencesKey("updatedAt")
        val BVN = stringPreferencesKey("bvn")
        val LAST_SYNC_TIME = stringPreferencesKey("last_syn_time")
        val CURRENT_WALLET_BALANCE = doublePreferencesKey("current_wallet_balance")
    }

}