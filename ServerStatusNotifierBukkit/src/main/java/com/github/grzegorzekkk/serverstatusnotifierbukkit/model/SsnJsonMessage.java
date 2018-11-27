package com.github.grzegorzekkk.serverstatusnotifierbukkit.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SsnJsonMessage<T> {
    private static final Gson GSON = new Gson();

    private MessageType status;
    private T data;

    public enum MessageType {
        AUTH_REQUEST,
        DATA_RESPONSE,
        UNAUTHORIZED_RESPONSE
    }

    public String toJsonString() {
        return GSON.toJson(this);
    }

    public static <T> SsnJsonMessage fromJsonString(String json, Class<T> clazz) {
        TypeToken token = TypeToken.getParameterized(SsnJsonMessage.class, clazz);
        return (SsnJsonMessage<T>) GSON.fromJson(json, token.getType());
    }
}
