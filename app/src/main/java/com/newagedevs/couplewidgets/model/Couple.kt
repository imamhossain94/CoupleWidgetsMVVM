package com.newagedevs.couplewidgets.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Couple (
    @PrimaryKey
    val id              : Long?,
    var frame           : Decorator?,
    var heart           : Decorator?,
    val nameColor       : Int?,
    val counterColor    : Int?,
    val you             : Person?,
    val partner         : Person?,
) : Parcelable




