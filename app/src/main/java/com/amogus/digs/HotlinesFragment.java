package com.amogus.digs;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

//this is the java fragment for hotlines fragment
public class HotlinesFragment extends Fragment {
    private ArrayList<String> contactNames, contactNumbers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //once this java fragment is called, it will display the hotlines fragment to the fragment container
        //which is the frame layout
        View view = inflater.inflate(R.layout.fragment_hotlines, container, false);

        readCSV();

        SearchView searchView = view.findViewById(R.id.searchView);
        ListView listView = view.findViewById(R.id.listView);

        //get the resource for the view of adapter view
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.listview_item, contactNames);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String contact = "tel:" + contactNumbers.get(position);
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse(contact));

                //check permission
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, 2);
                    return;
                }
                startActivity(callIntent);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                arrayAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                arrayAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return view;
    }

    private void readCSV() {
        InputStream inputStream = getResources().openRawResource(R.raw.hotlines);
        contactNames = new ArrayList<String>();
        contactNumbers = new ArrayList<String>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split(",");
                contactNames.add(row[0]);
                contactNumbers.add(row[1]);
            }
        }
        catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: " + ex);
        }
        finally {
            try {
                inputStream.close();
            }
            catch (IOException e) {
                throw new RuntimeException("Error while closing input stream: " + e);
            }
        }
    }
}