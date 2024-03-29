package com.amogus.digs;

import android.Manifest.permission;
import android.annotation.SuppressLint;
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
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.amogus.digs.utilities.AppUtils;
import com.amogus.digs.utilities.BluetoothUtils;
import com.amogus.digs.utilities.SharedPrefManager;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;

import static com.amogus.digs.utilities.SharedPrefManager.*;

//this is the java fragment for helpme fragment
public class HelpMeFragment extends Fragment {
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private BluetoothAdapter bluetoothAdapter;
    private ToggleButton btnHelp;
    private PulsatorLayout pulsatorLayout;

    private int originalVolume = 0; //the default should 0 to be equivalent of mute
    private final int DISCOVERABILITY_DURATION = 3600; //1 hour

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //once this java fragment is called, it will display the helpme fragment to the fragment container
        //which is the frame layout
        View view = inflater.inflate(R.layout.fragment_help_me, container, false);

        //intialize the SharedPrefUtils to use the shared preferences
        SharedPrefManager.initialize(getActivity());

        mediaPlayer = MediaPlayer.create(getActivity(), R.raw.help_me_morse_code);
        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //get the device's original volume when the app is launched
        originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        btnHelp = view.findViewById(R.id.btn_help);
        pulsatorLayout = view.findViewById(R.id.pulsator);

        btnHelp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //ask permissions first
                    if (isBluetoothPermissionsGranted()) {
                        //save the original bluetooth name
                        saveDeviceBluetoothName(bluetoothAdapter.getName());

                        //set the volume to max
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

                        //set the audio to play in loop
                        mediaPlayer.setLooping(true);
                        mediaPlayer.start();

                        //close bluetooth if enabled
                        if (bluetoothAdapter.isEnabled()) {
                            turnOnBluetooth(false);
                        }

                        //open bluetooth
                        turnOnBluetooth(true);
                        pulsatorLayout.start();
                    } else {
                        btnHelp.setChecked(false);
                    }
                } else {
                    //change the name of the bluetooth
                    bluetoothAdapter.setName(getDeviceBluetoothName());
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();

                        //prepare the audio resource
                        mediaPlayer.prepareAsync();
                    }

                    //close bluetooth
                    turnOnBluetooth(false);

                    pulsatorLayout.stop();
                }
            }
        });

        return view;
    }

    @SuppressLint("MissingPermission")
    private void turnOnBluetooth(boolean yes) {
        if (yes) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERABILITY_DURATION);
            startActivityForResult(discoverableIntent, 1); //show a dialog asking to open the bluetooth with 1hr discoverability
        } else {
            bluetoothAdapter.disable(); //close the bluetooth
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == DISCOVERABILITY_DURATION) {
            String name = getFullName().equals("") ? "DIG's User": getFullName();
            String contact = getContactNumber().equals("") ? "#": getContactNumber();

            //change the bluetooth name by combining the app's name, user's name, and contact
            bluetoothAdapter.setName(AppUtils.getApplicationName(getActivity()) + "::" +  name + "::" + contact);
        } else {
            btnHelp.setChecked(false);
        }
    }

    private boolean isBluetoothPermissionsGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if ((ActivityCompat.checkSelfPermission(getActivity(), permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(getActivity(), permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED)) {
                //will show the dialog permission again if denied
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

    //this is called when the activity is destroyed(ex: exiting the app/activity/layout)
    @Override
    public void onDestroy() {
        btnHelp.setChecked(false);

        //set the device's volume to original
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0);
        super.onDestroy();
    }
}