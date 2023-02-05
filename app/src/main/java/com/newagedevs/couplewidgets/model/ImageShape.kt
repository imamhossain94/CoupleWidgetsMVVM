package com.newagedevs.couplewidgets.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class ImageShape (
    @PrimaryKey
    val id       : Long?,
    var resID    : Int,
    var active   : Boolean
) : Parcelable