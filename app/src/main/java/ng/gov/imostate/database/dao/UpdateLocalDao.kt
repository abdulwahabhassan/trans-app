package ng.gov.imostate.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ng.gov.imostate.database.entity.TransactionEntity
import ng.gov.imostate.database.entity.UpdateEntity

@Dao
interface UpdateLocalDao {

    @Query("SELECT * FROM `update` ORDER BY time DESC")
    suspend fun getAllUpdates(): List<UpdateEntity>

    @Query("SELECT * FROM `update` WHERE id==:updateId")
    suspend fun getUpdate(updateId: Long): UpdateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUpdate(update: UpdateEntity)
}