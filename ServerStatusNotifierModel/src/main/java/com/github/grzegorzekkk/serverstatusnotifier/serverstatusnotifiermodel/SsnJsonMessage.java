package com.github.grzegorzekkk.serverstatusnotifier.serverstatusnotifiermodel;

import com.github.grzegorzekkk.serverstatusnotifier.serverstatusnotifiermodel.json.DateTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SsnJsonMessage<T> implements Serializable {
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(Date.class, new DateTypeAdapter()).create();

    private MessageType status;
    private T data;
    private Date creationDate = new Date();
    private UUID clientId;

    public enum MessageType {
        CONSOLE_REQUEST,
        CONSOLE_RESPONSE,
        DATA_REQUEST,
        DATA_RESPONSE,
        AUTH_REQUEST,
        AUTHORIZED_RESPONSE,
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
