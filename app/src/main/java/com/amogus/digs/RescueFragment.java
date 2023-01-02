package com.amogus.digs;

import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.*;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.kongqw.radarscanviewlibrary.RadarScanView;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import static android.bluetooth.BluetoothAdapter.*;


//this is the java fragment for rescue fragment
public class RescueFragment extends Fragment {
    private Singleton singleton;
    private ToggleButton btnSearch;
    private RadarScanView radarScanView;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDisplayAdapter bluetoothDisplayAdapter;
    private Timer bluetoothRefreshTimer;

    private boolean isBroadcastReceiverRegistered = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //once this java fragment is called, it will display the rescue fragment to the fragment container
        //which is the frame layout
        View view = inflater.inflate(R.layout.fragment_rescue, container, false);

        singleton = Singleton.getInstance(getActivity());

        btnSearch = view.findViewById(R.id.btn_search);
        radarScanView = view.findViewById(R.id.radarScanView);
        ListView listView = view.findViewById(R.id.list_bluetooths);
        bluetoothAdapter = getDefaultAdapter();

        bluetoothDisplayAdapter = new BluetoothDisplayAdapter();
        listView.setAdapter(bluetoothDisplayAdapter);

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
                    if (bluetoothRefreshTimer != null) {
                        bluetoothRefreshTimer.cancel();
                    }
                    if (bluetoothAdapter.isDiscovering()) {
                        bluetoothAdapter.cancelDiscovery();
                    }
                    unregisterBroadcastReceiver();
                    turnOnBluetooth(false);
                    bluetoothDisplayAdapter.clear();
                    bluetoothDisplayAdapter.notifyDataSetChanged();

                    radarScanView.stopScan();
                    radarScanView.setVisibility(View.INVISIBLE);
                }
            }
        });

        return view;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            registerBroadcastReceiver();
            bluetoothAdapter.startDiscovery();

            bluetoothRefreshTimer = new Timer();
            bluetoothRefreshTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bluetoothDisplayAdapter.clear();
                            bluetoothDisplayAdapter.notifyDataSetChanged();
                            if (bluetoothAdapter.isDiscovering()) {
                                bluetoothAdapter.cancelDiscovery();
                            }
                            bluetoothAdapter.startDiscovery();
                        }
                    });
                }
            }, 10000, 10000); //will start every 10 seconds, and will repeat every 10 seconds

            radarScanView.setVisibility(View.VISIBLE);
            radarScanView.startScan();
        } else {
            btnSearch.setChecked(false);
        }
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
            getActivity().unregisterReceiver(broadcastReceiver);
            isBroadcastReceiverRegistered = false;
        }
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(getTag(), intent.getAction());
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = bluetoothDevice.getName();
                if (deviceName != null) {
                    if (deviceName.startsWith(singleton.getAPP_NAME() + "::")) {
                        bluetoothDisplayAdapter.add(getNameInDeviceName(deviceName), getContactInDeviceName(deviceName));
                        bluetoothDisplayAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    };

    private String getNameInDeviceName(String deviceName) {
        String app_name = singleton.getAPP_NAME() + "::";
        String name = "";
        if (deviceName.startsWith(app_name)) {
            deviceName = deviceName.replace(app_name, "");
        }
        name = deviceName.substring(deviceName.indexOf("::") + 2);
        if (name.equals("")) {
            name = "DIGS' User";
        }
        return name;
    }

    private String getContactInDeviceName(String deviceName) {
        String app_name = singleton.getAPP_NAME() + "::";
        String contact = "";
        if (deviceName.startsWith(app_name)) {
            deviceName = deviceName.replace(app_name, "");
        }

        if (deviceName.startsWith("::")) {
            contact = "";
        } else {
            contact = deviceName.substring(0, deviceName.indexOf("::"));
        }
        return contact;
    }

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

    private void requestGPS() {
        if (!singleton.isGPS_Enabled()) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("GPS Permission")
                    .setMessage("GPS is required for this app to work. Please enable GPS")
                    .setCancelable(false)
                    .setPositiveButton("Yes", ((dialog, which) -> {
                        //will open the location access settings
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }))
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
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
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestGPS();
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
        requestGPS();
        if (singleton.isGPS_Enabled()) {
            requestLocationPermissions();
        }
        requestBluetoothPermissions();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        btnSearch.setChecked(false);
    }

    private class BluetoothDisplayAdapter extends BaseAdapter {

        private final ArrayList<String> usernames;
        private final ArrayList<String> contacts;

        public BluetoothDisplayAdapter() {
            usernames = new ArrayList<>();
            contacts = new ArrayList<>();
        }

        @Override
        public int getCount() {
            return usernames.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.listview_item_bluetooth, parent, false);
            }

            TextView username = convertView.findViewById(R.id.txt_username);
            TextView contact = convertView.findViewById(R.id.txt_contact);
            username.setText(usernames.get(position));
            contact.setText(contacts.get(position));
            return convertView;
        }

        public void add(String username, String contact) {
            usernames.add(username);
            contacts.add(contact);
        }

        public void clear() {
            usernames.clear();
            contacts.clear();
        }
    }
}