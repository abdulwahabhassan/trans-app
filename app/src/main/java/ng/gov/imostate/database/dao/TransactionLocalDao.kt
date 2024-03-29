package ng.gov.imostate.database.dao

import androidx.room.*
import ng.gov.imostate.database.entity.HolidayEntity
import ng.gov.imostate.database.entity.TransactionEntity

@Dao
interface TransactionLocalDao {
    @Query("SELECT * FROM `transaction`")
    suspend fun getAllTransactions(): List<TransactionEntity>

    @Query("SELECT * FROM `transaction` WHERE vehicle_id=:vehicleId")
    suspend fun getTransaction(vehicleId: String): TransactionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteAllTransactions(transactions: List<TransactionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTaxFreeDays(holidays: List<HolidayEntity>)

    @Delete
    suspend fun deleteAllTaxFreeDays(holidays: List<HolidayEntity>)

    @Query("SELECT * FROM holiday")
    suspend fun getAllTaxFreeDays(): List<HolidayEntity>
}