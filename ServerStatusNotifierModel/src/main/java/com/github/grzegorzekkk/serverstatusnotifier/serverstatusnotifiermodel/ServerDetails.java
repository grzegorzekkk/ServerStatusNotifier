package com.github.grzegorzekkk.serverstatusnotifier.serverstatusnotifiermodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerDetails implements Serializable {
    public static final String SERIAL_NAME = "SERVER_DETAILS";

    private SrvConnDetails connDetails;
    private String serverName;
    private boolean isOnline;
    private int playersCount;
    private int playersMax;
    private String serverVersion;

    public ServerDetails(SrvConnDetails connDetailsArg, boolean isOnlineArg) {
        connDetails = connDetailsArg;
        isOnline = isOnlineArg;
    }
}
