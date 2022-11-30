package com.amogus.digs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

//this is the java fragment for wiki fragment
public class WikiFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //once this java fragment is called, it will display the wiki fragment to the fragment container
        //which is the frame layout
        return inflater.inflate(R.layout.fragment_wiki, container, false);
    }
}