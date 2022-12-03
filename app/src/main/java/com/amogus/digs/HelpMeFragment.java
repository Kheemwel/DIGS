package com.amogus.digs;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
import androidx.fragment.app.Fragment;

//this is the java fragment for helpme fragment
public class HelpMeFragment extends Fragment {
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private BluetoothAdapter bluetoothAdapter;
    private ToggleButton btnHelp;
    private WifiHotspotManager hotspot;

    private int origionalVolume = 0;
    private String TAG = "MainActivity";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //once this java fragment is called, it will display the helpme fragment to the fragment container
        //which is the frame layout
        View view = inflater.inflate(R.layout.fragment_help_me, container, false);

        mediaPlayer = MediaPlayer.create(getActivity(), R.raw.original_nokia);
        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //hotspot = new WifiHotspotManager(getActivity().getApplicationContext());
        //hotspot.showWritePermissionSettings(true);

        //get the device's original volume when the app is launched
        origionalVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        btnHelp = view.findViewById(R.id.btn_help);
        btnHelp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //set the volume to max
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

                    //set the audio to play in loop
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();

                    //open bluetooth if disabled
                    if (!bluetoothAdapter.isEnabled()) {
                        bluetoothAdapter.enable();
                    }

//                    //turn on hotspot if close
//                    if (!hotspot.isWifiHotspotOn()) {
//                        boolean on = hotspot.turnWifiHotspotOn();
//                    }
                } else {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();

                        //prepare the audio resource
                        mediaPlayer.prepareAsync();
                    }

                    //close bluetooth if enabled
                    if (bluetoothAdapter.isEnabled()) {
                        bluetoothAdapter.disable();
                    }

//                    //turn off hotspot if open
//                    if (hotspot.isWifiHotspotOn()) {
//                        boolean off = hotspot.turnWifiHotspotOff();
//                    }
                }
            }
        });

        return view;
    }

    //this is called when the activity is stopped (ex: exiting the app/activity/layout)
    @Override
    public void onStop() {
        btnHelp.setChecked(false);

        //set the device's volume to original
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, origionalVolume, 0);
        super.onStop();
    }
}