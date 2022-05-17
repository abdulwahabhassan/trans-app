package ng.gov.imostate.database.dao

import androidx.room.*
import ng.gov.imostate.database.entity.DriverEntity
import ng.gov.imostate.database.entity.TransactionEntity
import ng.gov.imostate.database.entity.VehicleEntity
import retrofit2.http.DELETE

@Dao
interface TransactionLocalDao {
    @Query("SELECT * FROM `transaction`")
    suspend fun getAllTransactions(): List<TransactionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteAllTransactions(transactions: List<TransactionEntity>)
}