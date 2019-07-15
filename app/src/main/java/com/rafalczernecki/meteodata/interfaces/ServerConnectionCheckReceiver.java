package com.rafalczernecki.meteodata.interfaces;

public interface ServerConnectionCheckReceiver {
    String ARG_CONNECTION_STATUS = "connectionStatus";

    void receiveServerConnectionStatus(Integer connectionStatus);

    void setConnectionStatus(Integer connectionStatus);
}
