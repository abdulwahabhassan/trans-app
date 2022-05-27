package ng.gov.imostate.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import ng.gov.imostate.database.entity.TransactionEntity
import ng.gov.imostate.database.entity.UpdateEntity
import ng.gov.imostate.repository.AgentRepository
import ng.gov.imostate.repository.UserPreferencesRepository
import javax.inject.Inject

@HiltViewModel
class UpdatesFragmentViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository,
    private val agentRepository: AgentRepository
) : BaseViewModel(
    userPreferencesRepository
){

    suspend fun getAllUpdatesInDatabase(): List<UpdateEntity> {
        return agentRepository.getAllUpdatesInDatabase()
    }

    suspend fun insertUpdateToDatabase(update: UpdateEntity) {
        agentRepository.insertUpdateToDatabase(update)
    }

    suspend fun getUpdateInDatabase(updateId: Long): UpdateEntity? {
        return agentRepository.getUpdateInDatabase(updateId)
    }

}