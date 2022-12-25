package com.amogus.digs;

import android.Manifest;
import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.*;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.util.Timer;
import java.util.TimerTask;

import static android.bluetooth.BluetoothAdapter.*;


//this is the java fragment for rescue fragment
public class RescueFragment extends Fragment {
    private Singleton singleton;
    private ListView listView;
    private ToggleButton btnSearch;
    private BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter<String> arrayAdapter;
    private boolean isBroadcastReceiverRegistered = false;
    private Timer bluetoothRefreshTimer;

    private AlertDialog dialog;
    private static final String[] BLUETOOTH_PERMISSIONS_S = {permission.BLUETOOTH_SCAN, permission.BLUETOOTH_CONNECT};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //once this java fragment is called, it will display the rescue fragment to the fragment container
        //which is the frame layout
        View view = inflater.inflate(R.layout.fragment_rescue, container, false);

        singleton = Singleton.getInstance(getActivity());

        btnSearch = view.findViewById(R.id.btn_search);
        listView = view.findViewById(R.id.list_bluetooths);
        bluetoothAdapter = getDefaultAdapter();

        arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
        listView.setAdapter(arrayAdapter);

        btnSearch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (bluetoothAdapter.isEnabled()) {
                        turnOnBluetooth(false);
                    }
                    turnOnBluetooth(true);
                } else {
                    bluetoothRefreshTimer.cancel();
                    if (bluetoothAdapter.isDiscovering()) {
                        bluetoothAdapter.cancelDiscovery();
                    }
                    unregisterBroadcastReceiver();
                    turnOnBluetooth(false);
                    arrayAdapter.clear();
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        });

        return view;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        registerBroadcastReceiver();
        bluetoothAdapter.startDiscovery();

        bluetoothRefreshTimer = new Timer();
        bluetoothRefreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        arrayAdapter.clear();
                        arrayAdapter.notifyDataSetChanged();
                        if (bluetoothAdapter.isDiscovering()) {
                            bluetoothAdapter.cancelDiscovery();
                        }
                        bluetoothAdapter.startDiscovery();
                    }
                });
            }
        }, 10000, 10000); //will start every 10 seconds, and will repeat every 10 seconds
    }

    @SuppressLint("MissingPermission")
    private void turnOnBluetooth(boolean yes) {
        if (yes) {
            Intent openBluetooth = new Intent(ACTION_REQUEST_ENABLE);
            startActivityForResult(openBluetooth, 1);
        } else {
            bluetoothAdapter.disable();
        }
    }

    private void registerBroadcastReceiver() {
        if (!isBroadcastReceiverRegistered) {
            getActivity().registerReceiver(broadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            isBroadcastReceiverRegistered = true;
        }
    }

    private void unregisterBroadcastReceiver() {
        if (isBroadcastReceiverRegistered) {
            getActivity().registerReceiver(broadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            isBroadcastReceiverRegistered = false;
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(getTag(), intent.getAction());
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = bluetoothDevice.getName();
                String deviceAdress = bluetoothDevice.getAddress();
                Log.i(getTag(), deviceName + "\n" + deviceAdress);
                if (deviceName != null) {
                    arrayAdapter.add(bluetoothDevice.getName() + "\n" + bluetoothDevice.getAddress());
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    private void requestBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(getActivity(), permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{permission.BLUETOOTH_SCAN}, singleton.getREQUESTCODE_BLUETOOTH_PERMISSIONS());
            }
        }
    }

    private void requestLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(getActivity(), permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{permission.ACCESS_FINE_LOCATION}, singleton.getREQUESTCODE_LOCATION_PERMISSIONS());
        }

        if (ActivityCompat.checkSelfPermission(getActivity(), permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{permission.ACCESS_COARSE_LOCATION}, singleton.getREQUESTCODE_LOCATION_PERMISSIONS());
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AlertDialog permission_dialog;
        if (requestCode == singleton.getREQUESTCODE_LOCATION_PERMISSIONS()) {
            //this will run if the location access permission is denied
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                permission_dialog = new AlertDialog.Builder(getActivity())
                        .setTitle("Location Access Permission Needed")
                        .setMessage("Location Access is required for this app to work. Please grant the permission.")
                        .setCancelable(false)
                        .setNeutralButton("OK", ((dialog, which) -> dialog.dismiss()))
                        .show();
            }
        }

        if (requestCode == singleton.getREQUESTCODE_BLUETOOTH_PERMISSIONS()) {
            //this will run if the bluetooth permission is denied
            if (grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_DENIED) {
                permission_dialog = new AlertDialog.Builder(getActivity())
                        .setTitle("Bluetooth Permission Needed")
                        .setMessage("Bluetooth is required for this app to work. Please grant the permission.")
                        .setCancelable(false)
                        .setNeutralButton("OK", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        requestLocationPermissions();
        requestBluetoothPermissions();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterBroadcastReceiver();
    }
}