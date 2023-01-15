package com.amogus.digs;

import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.*;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.amogus.digs.utilities.AppUtils;
import com.amogus.digs.utilities.BluetoothUtils;
import com.amogus.digs.utilities.GPSUtils;
import com.kongqw.radarscanviewlibrary.RadarScanView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static android.bluetooth.BluetoothAdapter.*;


//this is the java fragment for rescue fragment
public class RescueFragment extends Fragment {
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
                    if (isGPS_Enabled() && isLocationPermissionsGranted() && isBluetoothPermissionsGranted()) {
                        if (bluetoothAdapter.isEnabled()) {
                            turnOnBluetooth(false);
                        }
                        turnOnBluetooth(true);
                    } else {
                        btnSearch.setChecked(false);
                    }
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
            Log.i(getTag(), action);
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = bluetoothDevice.getName() == null ? "N/A" : bluetoothDevice.getName();
                Log.i(getTag(), deviceName);
                if (deviceName.startsWith(AppUtils.getApplicationName(getActivity()) + "::")) {
                    bluetoothDisplayAdapter.add(deviceName);
                }
                bluetoothDisplayAdapter.notifyDataSetChanged();
            }
        }
    };

    private boolean isBluetoothPermissionsGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if ((ActivityCompat.checkSelfPermission(getActivity(), permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(getActivity(), permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED)) {
                if (shouldShowRequestPermissionRationale(permission.BLUETOOTH_CONNECT) || shouldShowRequestPermissionRationale(permission.BLUETOOTH_SCAN)) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Bluetooth Permission Needed")
                            .setMessage("Please grant the permission for Bluetooth access in order for this app to work properly.")
                            .setCancelable(false)
                            .setPositiveButton("OK", ((dialog, which) -> requestPermissions(new String[]{permission.BLUETOOTH_CONNECT, permission.BLUETOOTH_SCAN}, BluetoothUtils.REQUESTCODE_BLUETOOTH_PERMISSIONS)))
                            .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                            .show();
                } else {
                    requestPermissions(new String[]{permission.BLUETOOTH_CONNECT, permission.BLUETOOTH_SCAN}, BluetoothUtils.REQUESTCODE_BLUETOOTH_PERMISSIONS);
                }
                return false;
            }
        }
        return true;
    }

    private boolean isLocationPermissionsGranted() {
        if ((ActivityCompat.checkSelfPermission(getActivity(), permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(getActivity(), permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            if (shouldShowRequestPermissionRationale(permission.ACCESS_FINE_LOCATION) || shouldShowRequestPermissionRationale(permission.ACCESS_COARSE_LOCATION)) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Location Access Permission Required")
                        .setMessage("In order to use this feature, please grant location access permission.")
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialog, which) -> requestPermissions(new String[]{permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION}, GPSUtils.REQUESTCODE_LOCATION_PERMISSIONS))
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                        .show();
            } else {
                requestPermissions(new String[]{permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION}, GPSUtils.REQUESTCODE_LOCATION_PERMISSIONS);
            }
            return false;
        }
        return true;
    }

    private boolean isGPS_Enabled() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!isGPSEnabled) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("GPS")
                    .setMessage("This application requires GPS to work properly, do you want to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", ((dialog, which) -> {
                        //will open the location access settings
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);
                    }))
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        }
        return isGPSEnabled;
    }

    @Override
    public void onStart() {
        super.onStart();
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

        public void add(String deviceName) {
            String[] parts = deviceName.split("::");
            String username = parts[0];
            String contact = parts[1].equals("#") ? "" : parts[1];
            usernames.add(username);
            contacts.add(contact);
        }

        public void clear() {
            usernames.clear();
            contacts.clear();
        }
    }
}