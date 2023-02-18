package com.newagedevs.couplewidgets.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Couple(
    var active: Boolean,
    var frame: Decorator?,
    var heart: Decorator?,
    val nameColor: Int?,
    val counterColor: Int?,
    val you: Person?,
    val partner: Person?,
    val fallInLove: String?,
    val inRelation: String?,
) : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}

