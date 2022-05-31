package ng.gov.imostate.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "update")
data class UpdateEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    @ColumnInfo
    val title: String?,
    @ColumnInfo
    val body: String?,
    @ColumnInfo
    val time: String?
        )