package ru.sedavnyh.vkgroupscan.models.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import ru.sedavnyh.vkgroupscan.util.Constants.GROUPS_TABLE

@Entity(tableName = GROUPS_TABLE)
@Parcelize
data class GroupEntity (
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    var postCount : Int,
    var title: String,
    var avatar: String
) : Parcelable {
    override fun toString(): String {
        return title
    }
}