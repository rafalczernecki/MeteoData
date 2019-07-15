package com.rafalczernecki.meteodata.entities;

import java.util.ArrayList;
import java.util.Map;

public class Server {
    public static final int CONNECTION_UNDEFINED = 0;
    public static final int CONNECTION_CHECKING = 1;
    public static final int CONNECTION_OK = 2;
    public static final int CONNECTION_ERROR = 3;
    public static final int CONNECTION_DOWNLOADING_DATA = 4;
    private String serverTag;
    private String serverIpAddress;

    public Server(String serverTag, String serverIpAddress) {
        this.serverTag = serverTag;
        this.serverIpAddress = serverIpAddress;
    }

    public String getServerTag() {
        return serverTag;
    }

    public String getServerIpAddress() {
        return serverIpAddress;
    }

    public static boolean validateIPAddress(String ipAddress) {
        String[] tokens = ipAddress.split("\\.");
        if (tokens.length != 4) return false;
        for (String str : tokens) {
            int i = Integer.parseInt(str);
            if ((i < 0) || (i > 255)) return false;
        }
        return true;
    }

    public static ArrayList<String> getServerTagsArrayList(Map<String, ?> servers) {
        return new ArrayList<>(servers.keySet());
    }

    public static ArrayList<String> getServerIpAddressesArrayList(Map<String, ?> servers) {
        ArrayList<String> ipAddresses = new ArrayList<>();
        for (Object value : servers.values()) {
            ipAddresses.add(value.toString());
        }
        return ipAddresses;
    }
}
