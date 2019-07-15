package com.rafalczernecki.meteodata.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.rafalczernecki.meteodata.entities.Server;

import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreferencesHelper {

    private static final String ALL_SERVERS_SHARED_PREFERENCES = "allServersSharedPreferences";
    private static final String CURRENT_SERVER_SHARED_PREFERENCES = "currentServerSharedPreferences";
    private static final String CURRENT_SERVER_TAG = "currentServerTag";
    private static final String CURRENT_SERVER_IP_ADDRESS = "currentServerIpAddress";

    private Context context;
    private SharedPreferences allServersSharedPreferences;
    private SharedPreferences currentServerSharedPreferences;
    private SharedPreferences.Editor allServersSharedPreferencesEditor;
    private SharedPreferences.Editor currentServerSharedPreferencesEditor;

    public SharedPreferencesHelper(Context context) {
        this.context = context;
    }

    public void addNewServer(Server server) {
        instantiateAllServersSharedPreferencesIfNeeded();
        allServersSharedPreferencesEditor.putString(server.getServerTag(), server.getServerIpAddress());
        allServersSharedPreferencesEditor.apply();
    }

    public String getCurrentServerTag() {
        instantiateCurrentServerSharedPreferencesIfNeeded();
        return currentServerSharedPreferences.getString(CURRENT_SERVER_TAG, "");
    }

    public String getCurrentServerIpAddress() {
        instantiateCurrentServerSharedPreferencesIfNeeded();
        return currentServerSharedPreferences.getString(CURRENT_SERVER_IP_ADDRESS, "");
    }

    public Map<String, ?> getAllServersData() {
        instantiateAllServersSharedPreferencesIfNeeded();
        return allServersSharedPreferences.getAll();
    }

    public void changeCurrentServer(Server server) {
        instantiateCurrentServerSharedPreferencesIfNeeded();
        currentServerSharedPreferencesEditor.putString(CURRENT_SERVER_TAG, server.getServerTag());
        currentServerSharedPreferencesEditor.putString(CURRENT_SERVER_IP_ADDRESS, server.getServerIpAddress());
        currentServerSharedPreferencesEditor.apply();
    }

    public boolean deleteServer(Server server) {
        instantiateAllServersSharedPreferencesIfNeeded();
        instantiateCurrentServerSharedPreferencesIfNeeded();
        if (!currentServerSharedPreferences.getString(CURRENT_SERVER_TAG, "").equals(server.getServerTag())) {
            allServersSharedPreferencesEditor.remove(server.getServerTag());
            allServersSharedPreferencesEditor.apply();
            return true;
        }
        return false;
    }

    private void instantiateAllServersSharedPreferencesIfNeeded() {
        if (allServersSharedPreferences == null) allServersSharedPreferences = context
                .getSharedPreferences(ALL_SERVERS_SHARED_PREFERENCES, MODE_PRIVATE);
        if (allServersSharedPreferencesEditor == null) {
            allServersSharedPreferencesEditor = allServersSharedPreferences.edit();
        }
    }

    private void instantiateCurrentServerSharedPreferencesIfNeeded() {
        currentServerSharedPreferences = context
                .getSharedPreferences(CURRENT_SERVER_SHARED_PREFERENCES, MODE_PRIVATE);
        currentServerSharedPreferencesEditor = currentServerSharedPreferences.edit();
    }
}
