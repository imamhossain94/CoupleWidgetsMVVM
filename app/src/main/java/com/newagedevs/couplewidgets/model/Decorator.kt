package com.newagedevs.couplewidgets.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Decorator (
    val vector   : Int?,
    val color    : Int?,
) : Parcelable