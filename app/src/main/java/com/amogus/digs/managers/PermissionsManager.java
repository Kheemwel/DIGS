package com.amogus.digs.managers;

public class PermissionsManager {
    private static PermissionsManager permissionsManagerInstance;

    private PermissionsManager() {
    }

    public static PermissionsManager getInstance() {
        if (permissionsManagerInstance == null) {
            permissionsManagerInstance = new PermissionsManager();
        }
        return permissionsManagerInstance;
    }
}
