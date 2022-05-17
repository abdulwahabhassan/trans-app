package ng.gov.imostate.database.dao

import androidx.room.*
import ng.gov.imostate.database.entity.RateEntity
import ng.gov.imostate.database.entity.TransactionEntity

@Dao
interface RateLocalDao {

    @Query("SELECT * FROM rate")
    suspend fun getAllRates(): List<RateEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRates(rates: List<RateEntity>)

    @Query("SELECT * FROM rate WHERE rate.category LIKE :category")
    suspend fun getRate(category: String): RateEntity?
}