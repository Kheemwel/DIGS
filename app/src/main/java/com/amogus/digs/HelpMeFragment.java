package com.amogus.digs;

import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

//this is the java fragment for helpme fragment
public class HelpMeFragment extends Fragment {
    private Singleton singleton;
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private BluetoothAdapter bluetoothAdapter;
    private ToggleButton btnHelp;

    private int originalVolume = 0;
    private static final String[] BLUETOOTH_PERMISSIONS_S = {permission.BLUETOOTH_SCAN, permission.BLUETOOTH_CONNECT};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //once this java fragment is called, it will display the helpme fragment to the fragment container
        //which is the frame layout
        View view = inflater.inflate(R.layout.fragment_help_me, container, false);

        singleton = Singleton.getInstance(getActivity());

        mediaPlayer = MediaPlayer.create(getActivity(), R.raw.original_nokia);
        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //get the device's original volume when the app is launched
        originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        btnHelp = view.findViewById(R.id.btn_help);
        btnHelp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //set the volume to max
                    //audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

                    //set the audio to play in loop
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();

                    //close bluetooth if enabled
                    if (bluetoothAdapter.isEnabled()) {
                        turnOnBluetooth(false);
                    }
                    //open bluetooth
                    turnOnBluetooth(true);
                } else {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();

                        //prepare the audio resource
                        mediaPlayer.prepareAsync();
                    }

                    //close bluetooth
                    turnOnBluetooth(false);
                }
            }
        });

        return view;
    }

    @SuppressLint("MissingPermission")
    private void turnOnBluetooth(boolean yes) {
        if (yes) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivityForResult(discoverableIntent, 1);
        } else {
            bluetoothAdapter.disable();
            bluetoothAdapter.setName(singleton.getDeviceBluetoothName());
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (bluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
                bluetoothAdapter.setName(singleton.getAPP_NAME() + "::" + singleton.getContact_number() + "::" + singleton.getUser_name());
            }
        } else {
            btnHelp.setChecked(false);
        }
    }

    private void requestBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(getActivity(), permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{permission.BLUETOOTH_CONNECT}, singleton.getREQUESTCODE_BLUETOOTH_PERMISSIONS());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AlertDialog permission_dialog;
        if (requestCode == singleton.getREQUESTCODE_BLUETOOTH_PERMISSIONS()) {
            //this will run if the permission is denied
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                permission_dialog = new AlertDialog.Builder(getActivity())
                        .setTitle("Bluetooth Permission Needed")
                        .setMessage("Bluetooth is required for this app to work. Please grant the permission.")
                        .setCancelable(false)
                        .setNeutralButton("OK", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onStart() {
        super.onStart();
        requestBluetoothPermissions();
        singleton.saveDeviceBluetoothName(bluetoothAdapter.getName());
    }

    //this is called when the activity is stopped (ex: exiting the app/activity/layout)
    @Override
    public void onStop() {
        btnHelp.setChecked(false);

        //set the device's volume to original
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0);
        super.onStop();
    }
}