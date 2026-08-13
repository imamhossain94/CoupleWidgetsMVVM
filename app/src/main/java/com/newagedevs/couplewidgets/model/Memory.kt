package com.newagedevs.couplewidgets.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

/**
 * A date the couple wants to remember — a first date, an anniversary, a trip.
 *
 * [date] is `yyyy-MM-dd`, matching the format already used for `Couple.inRelation`.
 *
 * [iconName] is a stable key into `EventIconCatalog`, never a resource ID —
 * resource IDs are reassigned whenever drawables are added or removed. See
 * `DecoratorCatalog` for the crash that taught us this.
 *
 * [repeatsYearly] marks events that come round every year (anniversaries,
 * birthdays) so the countdown targets the next occurrence rather than a date
 * that has already passed.
 */
@Entity
@Parcelize
data class Memory(
    val title: String,
    val date: String,
    val iconName: String?,
    val note: String?,
    val repeatsYearly: Boolean = false,
) : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
