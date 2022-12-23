package com.amogus.digs;

import android.Manifest;
import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ToggleButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import static android.bluetooth.BluetoothAdapter.*;


//this is the java fragment for rescue fragment
public class RescueFragment extends Fragment {

    private ListView listView;
    private ToggleButton btnSearch;
    private BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter<String> arrayAdapter;
    private boolean isBroadcastReceiverRegistered = false;
    private static final String[] BLUETOOTH_PERMISSIONS_S = {Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //once this java fragment is called, it will display the rescue fragment to the fragment container
        //which is the frame layout
        View view = inflater.inflate(R.layout.fragment_rescue, container, false);

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
                    if (!bluetoothAdapter.isEnabled()) {
                        turnOnBluetooth(true);
                    }
                } else {
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
    }

    private void turnOnBluetooth(boolean yes) {
        if (yes) {
            Intent openBluetooth = new Intent(ACTION_REQUEST_ENABLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Bluetooth Permission")
                        .setMessage("Bluetooth is required for this device discoverability. Please enable Bluetooth")
                        .setCancelable(false)
                        .setPositiveButton("Yes", ((dialog, which) -> {
                            //will ask permission if the device android version is 12 and above
                            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                ActivityCompat.requestPermissions(getActivity(), BLUETOOTH_PERMISSIONS_S, 2);
                                return;
                            }
                            startActivityForResult(openBluetooth, 1);
                        }))
                        .show();
            } else {
                startActivityForResult(openBluetooth, 1);
            }
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
                Log.i(getTag(), bluetoothDevice.getName() + "\n" + bluetoothDevice.getAddress());
                arrayAdapter.add(bluetoothDevice.getName() + "\n" + bluetoothDevice.getAddress());
                arrayAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(getActivity(), permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{permission.ACCESS_FINE_LOCATION}, 2);
        }
        if (ActivityCompat.checkSelfPermission(getActivity(), permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{permission.ACCESS_COARSE_LOCATION}, 4);
        }
        if (ActivityCompat.checkSelfPermission(getActivity(), permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{permission.BLUETOOTH_ADMIN}, 3);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 2: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   //bluetoothAdapter.startDiscovery();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterBroadcastReceiver();
    }
}