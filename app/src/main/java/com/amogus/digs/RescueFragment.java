package com.amogus.digs;

import android.Manifest;
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
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Set;


//this is the java fragment for rescue fragment
public class RescueFragment extends Fragment {

    private ListView listView;
    private ToggleButton btnSearch;
    private BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> arrayList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //once this java fragment is called, it will display the rescue fragment to the fragment container
        //which is the frame layout
        View view = inflater.inflate(R.layout.fragment_rescue, container, false);

        btnSearch = view.findViewById(R.id.btn_search);
        listView = view.findViewById(R.id.list_bluetooths);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);

        btnSearch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!bluetoothAdapter.isEnabled()) {
                        turnOnBluetooth(true);
                    }
                } else {
                    turnOnBluetooth(false);
                }
            }
        });

        return view;
    }

    private void turnOnBluetooth(boolean yes) {
        if (yes) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Bluetooth Permission")
                    .setMessage("Bluetooth is required for this app to work. Please enable Bluetooth")
                    .setCancelable(false)
                    .setPositiveButton("Yes", ((dialog, which) -> {
                        //will ask permission if the device android version is 12 and above
                        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.BLUETOOTH_ADMIN}, 3);
                            return;
                        }
                        bluetoothAdapter.enable();
                        if (checkCoarseLocationPermission()) {
                            arrayList.clear();
                            arrayAdapter.notifyDataSetChanged();
                            getActivity().registerReceiver(broadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
                            bluetoothAdapter.startDiscovery();

                            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                            for (BluetoothDevice bluetoothDevice : pairedDevices) {
                                arrayList.add(bluetoothDevice.getName());
                                System.out.println(bluetoothDevice.getName());
                            }
                            arrayAdapter.notifyDataSetChanged();
                        }
                    }))
                    .show();
        } else {
            //getActivity().unregisterReceiver(broadcastReceiver);
            bluetoothAdapter.cancelDiscovery();
            bluetoothAdapter.disable();
        }
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
                    return;
                }
                arrayList.add(device.getName());
                System.out.println("Devices: " + device.getName());
                arrayAdapter.notifyDataSetChanged();
            }
        }
    };

    private boolean checkCoarseLocationPermission() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 4);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(broadcastReceiver);
    }
}