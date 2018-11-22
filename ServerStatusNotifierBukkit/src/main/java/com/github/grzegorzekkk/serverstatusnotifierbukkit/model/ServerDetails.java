package com.github.grzegorzekkk.serverstatusnotifierbukkit.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ServerDetails {
    private ServerStatus serverStatus;
    private int playersCount;
}
