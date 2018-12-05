package com.github.grzegorzekkk.serverstatusnotifier.serverstatusnotifiermodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SrvConnDetails implements Serializable {
    private String address;
    private int port;
    private String password;
}
