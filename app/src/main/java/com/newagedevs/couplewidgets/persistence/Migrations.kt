package com.newagedevs.couplewidgets.persistence

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Schema migrations.
 *
 * These matter more than usual here: the database is built with
 * `fallbackToDestructiveMigration()`, so **any** version bump without a matching
 * migration silently deletes every saved widget. Adding the `Memory` table in v6
 * is purely additive, so v5 data carries over untouched.
 *
 * The DDL must match what Room generates for the entity exactly (column order,
 * nullability, affinities), or Room's startup schema validation will throw.
 */
val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS `Memory` (" +
                "`title` TEXT NOT NULL, " +
                "`date` TEXT NOT NULL, " +
                "`iconName` TEXT, " +
                "`note` TEXT, " +
                "`repeatsYearly` INTEGER NOT NULL, " +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)"
        )
    }
}

/**
 * Adds the per-widget solid background color. Purely additive — the new nullable
 * column defaults to NULL on existing rows, which the renderer reads as "use the
 * theme default color", so v6 data is untouched.
 */
val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `Couple` ADD COLUMN `widgetBackgroundColor` INTEGER")
    }
}

/** Every migration the app knows about, applied to both database builders. */
val ALL_MIGRATIONS = arrayOf(MIGRATION_5_6, MIGRATION_6_7)
