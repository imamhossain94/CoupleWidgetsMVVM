package com.newagedevs.couplewidgets.persistence

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.room.TypeConverter
import com.google.gson.JsonObject
import com.newagedevs.couplewidgets.model.Decorator
import com.newagedevs.couplewidgets.model.Person
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
        val image = obj.get("image").asString

        // Base64 string to bitmap
        val base64 = Base64.decode(image, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(base64, 0, base64.size)

        return Person(name, birthday, bitmap)
    }

    @TypeConverter
    fun fromObject(obj: Person): String {

        // Bitmap to base64 string
        val byteArrayOutputStream = ByteArrayOutputStream()
        obj.image?.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byte = byteArrayOutputStream.toByteArray()
        val base64 = Base64.encodeToString(byte, Base64.DEFAULT)

        return "{ 'name': ${obj.name}, 'birthday': ${obj.birthday}, 'image': $base64 }"
    }
}
