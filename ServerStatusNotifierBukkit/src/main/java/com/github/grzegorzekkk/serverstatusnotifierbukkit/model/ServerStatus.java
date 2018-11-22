package com.github.grzegorzekkk.serverstatusnotifierbukkit.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ServerStatus {
    private String serverName;
    private boolean isOnline;
}
