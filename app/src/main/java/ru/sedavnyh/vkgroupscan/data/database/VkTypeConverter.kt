package ru.sedavnyh.vkgroupscan.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.sedavnyh.vkgroupscan.models.wallGetCommentsModel.Comment
import ru.sedavnyh.vkgroupscan.models.wallGetCommentsModel.RespondThread

class VkTypeConverter {
    var gson = Gson()

    // comments
    @TypeConverter
    fun threadToString(thread: RespondThread?) : String? {
        return gson.toJson(thread)
    }

    @TypeConverter
    fun stringToThread(data: String?) : RespondThread? {
        val listType = object : TypeToken<RespondThread>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun listStringToString(list: List<String>) : String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun stringToListString(data: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun mutableListCommentToString(list: MutableList<Comment>) : String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun stringToMutableListComment(data: String): MutableList<Comment> {
        val listType = object : TypeToken<MutableList<Comment>>() {}.type
        return gson.fromJson(data, listType)
    }
}