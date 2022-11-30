package com.amogus.digs;

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
    private MediaPlayer mp;
    private AudioManager am;
    private int origionalVolume = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //once this java fragment is called, it will display the helpme fragment to the fragment container
        //which is the frame layout
        View view = inflater.inflate(R.layout.fragment_help_me, container, false);

        mp = MediaPlayer.create(getActivity(), R.raw.original_nokia);
        am = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        //get the device's original volume when the app is launched
        origionalVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);

        ToggleButton btnHelp = view.findViewById(R.id.btn_help);
        btnHelp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //set the volume to max
                    am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

                    //set the audio to play in loop
                    mp.setLooping(true);
                    mp.start();
                } else {
                    if (mp.isPlaying()) {
                        mp.stop();

                        //prepare the audio resource
                        mp.prepareAsync();
                    }
                }
            }
        });

        return view;
    }

    //this is called when the activity is destroyed (ex: exiting the app)
    @Override
    public void onDestroy() {
        if (mp.isPlaying()) {
            mp.stop();

            //prepare the audio resource
            mp.prepareAsync();
        }

        //set the device's volume to original
        am.setStreamVolume(AudioManager.STREAM_MUSIC, origionalVolume, 0);
        super.onDestroy();
    }
}