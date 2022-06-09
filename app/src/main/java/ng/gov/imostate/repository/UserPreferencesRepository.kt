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
            bvn = preferences[PreferencesKeys.BVN],
            lastSyncTime = preferences[PreferencesKeys.LAST_SYNC_TIME],
            currentWalletBalance = preferences[PreferencesKeys.CURRENT_WALLET_BALANCE],
            currentTotalVended = preferences[PreferencesKeys.CURRENT_TOTAL_VENDED],
            currentTotalCredited = preferences[PreferencesKeys.CURRENT_TOTAL_CREDITED],
            currentPayable = preferences[PreferencesKeys.CURRENT_PAYABLE],
            currentPaidOut = preferences[PreferencesKeys.CURRENT_PAID_OUT],
            collectionSetting = preferences[PreferencesKeys.COLLECTION_SETTING],
            instalmentsSetting = preferences[PreferencesKeys.INSTALMENT_SETTING]
        )
    }

    suspend fun updateCurrentWalletInfo(
        balance: Double, vended: Double, credited: Double, payable: Double, paidOut: Double
    ) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.CURRENT_WALLET_BALANCE] = balance
            preferences[PreferencesKeys.CURRENT_TOTAL_VENDED] = vended
            preferences[PreferencesKeys.CURRENT_TOTAL_CREDITED] = credited
            preferences[PreferencesKeys.CURRENT_PAYABLE] = payable
            preferences[PreferencesKeys.CURRENT_PAID_OUT] = paidOut
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

    suspend fun updateAgentCollectionSettingValue(value: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.COLLECTION_SETTING] = value
        }
    }

    suspend fun updateInstalmentsSettingValue(value: String) {
        Timber.d("instalments: $value")
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.INSTALMENT_SETTING] = value != "0"
        }
    }

    suspend fun updateUserPreferences(userPreferences: UserPreferences) {
        dataStore.edit { preferences ->
           with(userPreferences) {
               preferences[PreferencesKeys.TOKEN] = token ?: ""
               preferences[PreferencesKeys.AGENT_NAME] = agentName ?: ""
               preferences[PreferencesKeys.AGENT_ID] = agentId ?: 0
               preferences[PreferencesKeys.PHOTO] = photo ?: ""
               preferences[PreferencesKeys.AGENT_FIRST_NAME] = agentFirstName ?: ""
               preferences[PreferencesKeys.AGENT_MIDDLE_NAME] = agentMiddleName ?: ""
               preferences[PreferencesKeys.AGENT_LAST_NAME] = agentLastName ?: ""
               preferences[PreferencesKeys.TYPE] = type ?: ""
               preferences[PreferencesKeys.PHONE] = phone ?: ""
               preferences[PreferencesKeys.ADDRESS] = address ?: ""
               preferences[PreferencesKeys.ONBOARDING_DATE] = onboardingDate ?: ""
               preferences[PreferencesKeys.EMAIL] = email ?: ""
               preferences[PreferencesKeys.EMAIL_VERIFIED_AT] = emailVerifiedAt ?: ""
               preferences[PreferencesKeys.STATUS] = status ?: ""
               preferences[PreferencesKeys.BVN] = bvn ?: ""
           }
        }
    }

    data class UserPreferences(
        val token: String? = "",
        val loggedIn: Boolean? = false,
        val agentName: String? = "",
        val agentId: Long? = 0,
        val photo: String? = "",
        val agentFirstName: String? = "",
        val agentMiddleName: String? = "",
        val agentLastName: String? = "",
        val type: String? = "",
        val phone: String? = "",
        val address: String? = "",
        val onboardingDate: String? = "",
        val email:String? = "",
        val emailVerifiedAt: String? = "",
        val status: String? = "",
        val bvn: String? = "",
        val lastSyncTime: String? = "",
        val currentWalletBalance: Double? = 0.00,
        val currentTotalVended: Double? = 0.00,
        val currentTotalCredited: Double? = 0.00,
        val currentPayable: Double? = 0.00,
        val currentPaidOut: Double? = 0.00,
        val collectionSetting: String? = "",
        val instalmentsSetting: Boolean? = false
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
        val BVN = stringPreferencesKey("bvn")
        val LAST_SYNC_TIME = stringPreferencesKey("last_syn_time")
        val CURRENT_WALLET_BALANCE = doublePreferencesKey("current_wallet_balance")
        val CURRENT_TOTAL_VENDED = doublePreferencesKey("current_total_vended")
        val CURRENT_TOTAL_CREDITED = doublePreferencesKey("current_total_credited")
        val CURRENT_PAYABLE = doublePreferencesKey("current_payable")
        val CURRENT_PAID_OUT = doublePreferencesKey("current_paid_out")
        val COLLECTION_SETTING = stringPreferencesKey("collectionSetting")
        val INSTALMENT_SETTING = booleanPreferencesKey("instalments")
    }

}