package ng.gov.imostate.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "update")
data class UpdateEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val title: String?,
    val body: String?,
    val time: String?
        )