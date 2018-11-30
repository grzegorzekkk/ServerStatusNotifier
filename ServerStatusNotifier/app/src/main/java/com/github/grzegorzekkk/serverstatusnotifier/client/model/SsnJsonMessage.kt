package com.github.grzegorzekkk.serverstatusnotifier.client.model

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SsnJsonMessage<T>(statusArg: MessageType = MessageType.AUTH_REQUEST, dataArg: T? = null) {
    var status: MessageType = statusArg
    var data: T? = dataArg

    enum class MessageType {
        AUTH_REQUEST,
        DATA_RESPONSE,
        UNAUTHORIZED_RESPONSE
    }

    fun toJsonString(): String {
        return gson.toJson(this)
    }

    companion object {
        private val gson = Gson()

        fun <T> fromJsonString(json: String, clazz: Class<T>): SsnJsonMessage<T> {
            val token = TypeToken.getParameterized(SsnJsonMessage::class.java, clazz)
            return gson.fromJson(json, token.type) as SsnJsonMessage<T>
        }
    }
}