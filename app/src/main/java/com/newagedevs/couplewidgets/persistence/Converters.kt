package com.newagedevs.couplewidgets.persistence

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.newagedevs.couplewidgets.model.Decorator
import com.newagedevs.couplewidgets.model.Person
import org.json.JSONObject
import timber.log.Timber
import java.io.ByteArrayOutputStream


class DecoratorConverter {

    @TypeConverter
    fun fromString(value: String): Decorator? {
        val objectType = object : TypeToken<Decorator>() {}.type
        return Gson().fromJson<Decorator>(value, objectType)
    }

    @TypeConverter
    fun fromObject(obj: Decorator): String {
        val gson = Gson()
        return gson.toJson(obj)
    }
}

class PersonConverter {

    @TypeConverter
    fun fromString(value: String): Person {

        val obj: JsonObject = Gson().fromJson(value, JsonObject::class.java)

        val name = obj.get("name").asString
        val birthday = obj.get("birthday").asString
        val image = Uri.parse(obj.get("image").asString) ?: null

        return Person(name, birthday, image)
    }

    @TypeConverter
    fun fromObject(obj: Person): String {

        val uriString: String = obj.image.toString()
        val jsonObject = JSONObject("{\"name\":\"${obj.name}\", \"birthday\":\"${obj.birthday}\", \"image\":\"$uriString\"}")

        return jsonObject.toString()
    }
}
