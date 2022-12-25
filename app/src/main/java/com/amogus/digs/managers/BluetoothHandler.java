package com.amogus.digs.managers;

public class BluetoothHandler {
    private static BluetoothHandler bluetoothHandlerInstance;

    private BluetoothHandler() {
    }

    public static BluetoothHandler getInstance() {
        if (bluetoothHandlerInstance == null) {
            bluetoothHandlerInstance = new BluetoothHandler();
        }
        return bluetoothHandlerInstance;
    }
}
