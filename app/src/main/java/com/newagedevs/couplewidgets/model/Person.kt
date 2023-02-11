package com.newagedevs.couplewidgets.model

import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Person (
    val name       : String?,
    val birthday   : String?,
    val image      : Uri?,
) : Parcelable
