package ru.sedavnyh.vkgroupscan.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.sedavnyh.vkgroupscan.models.wallGetModel.Attachment

class VkTypeConverter {

    var gson = Gson()

    @TypeConverter
    fun listAttachmentsToString(list: List<Attachment>?): String? {
        return gson.toJson(list)
    }

    @TypeConverter
    fun stringToListAttachments(data: String?): List<Attachment>? {
        val listType = object : TypeToken<List<Attachment>>() {}.type
        return gson.fromJson(data, listType)
    }
}