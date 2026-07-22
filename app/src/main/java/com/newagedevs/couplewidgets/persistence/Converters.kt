package com.newagedevs.couplewidgets.persistence

import android.net.Uri
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.newagedevs.couplewidgets.extensions.isUriEmpty
import com.newagedevs.couplewidgets.model.Decorator
import com.newagedevs.couplewidgets.model.Person
import com.newagedevs.couplewidgets.utils.DecoratorCatalog
import org.json.JSONObject


/**
 * Decorators are persisted by stable resource *name*, not by resource ID — IDs are
 * reassigned whenever drawables are added or removed. See [DecoratorCatalog].
 *
 * This is a JSON payload inside an unchanged TEXT column, so adding the name field
 * needs no schema version bump (which matters: the database is built with
 * `fallbackToDestructiveMigration`, so a bump would wipe every saved widget).
 */
class DecoratorConverter {

    @TypeConverter
    fun fromString(value: String): Decorator? {
        val objectType = object : TypeToken<Decorator>() {}.type
        val decorator = Gson().fromJson<Decorator>(value, objectType) ?: return null

        // Prefer the stable name; fall back to the stored ID for legacy rows
        // written before names existed (callers still guard with safeShape/safeSymbol).
        val resolved = DecoratorCatalog.idFor(decorator.name)
        return if (resolved != null) decorator.copy(vector = resolved) else decorator
    }

    @TypeConverter
    fun fromObject(obj: Decorator): String {
        // In memory `vector` is authoritative, so always re-derive the name from it —
        // otherwise a stale name carried on a copied Decorator would silently win
        // over a newly picked shape. Keep any existing name only if the current
        // vector isn't a catalog member.
        val named = obj.copy(name = DecoratorCatalog.nameFor(obj.vector) ?: obj.name)

        return Gson().toJson(named)
    }
}

class PersonConverter {

    @TypeConverter
    fun fromString(value: String): Person {

        val obj: JsonObject = Gson().fromJson(value, JsonObject::class.java)

        val name = obj.get("name").asString
        val birthday = obj.get("birthday").asString
        val image = obj.get("image").asString

        val imageUri: Uri? = if (image == "null") Uri.EMPTY else Uri.parse(image)

        return Person(name, birthday, imageUri)
    }

    @TypeConverter
    fun fromObject(obj: Person): String {

        val uriString: String? = if (isUriEmpty(obj.image)) null else obj.image.toString()

        val jsonObject =
            JSONObject("{\"name\":\"${obj.name}\", \"birthday\":\"${obj.birthday}\", \"image\":\"$uriString\"}")

        return jsonObject.toString()
    }
}
