package com.amogus.digs.managers;

public class InternalStorageManager {
    private static InternalStorageManager internalStorageManagerInstance;

    private InternalStorageManager() {
    }

    public static InternalStorageManager getInstance() {
        if (internalStorageManagerInstance == null) {
            internalStorageManagerInstance = new InternalStorageManager();
        }
        return internalStorageManagerInstance;
    }
}
