package com.newagedevs.couplewidgets.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * A user-chosen frame shape or heart symbol.
 *
 * [vector] is a live `R.drawable.*` ID, valid only for the running build — never
 * treat it as the persisted source of truth. [name] is the stable resource name
 * (e.g. `"shape_4"`) that survives resource renumbering; `DecoratorConverter`
 * fills it in on write and resolves [vector] back from it on read.
 * See `DecoratorCatalog` for why.
 */
@Parcelize
data class Decorator (
    val vector   : Int?,
    val color    : Int?,
    val name     : String? = null,
) : Parcelable
