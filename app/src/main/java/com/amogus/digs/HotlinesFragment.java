package com.amogus.digs;

import android.Manifest.permission;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import org.w3c.dom.Text;

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

        EditText searchText = view.findViewById(R.id.searchText);
        ListView listView = view.findViewById(R.id.list_hotlines);

        //get the resource for the view of adapter view
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.listview_item_hotlines, contactNames);
//        listView.setAdapter(arrayAdapter);

        HotlinesDisplayAdapter hotlinesDisplayAdapter = new HotlinesDisplayAdapter(contactNames, contactNumbers);
        listView.setAdapter(hotlinesDisplayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String contact = "tel:" + contactNumbers.get(position);
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse(contact));

                //check permission
                if (ActivityCompat.checkSelfPermission(getActivity(), permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    if (shouldShowRequestPermissionRationale(permission.CALL_PHONE)) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Call Permission Needed")
                                .setMessage("Call is required for this app to work. Please grant the permission.")
                                .setCancelable(false)
                                .setPositiveButton("OK", ((dialog, which) -> requestPermissions(new String[]{permission.CALL_PHONE}, 1)))
                                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                                .show();
                    } else {
                        requestPermissions(new String[]{permission.CALL_PHONE}, 1);
                    }
                    return;
                }
                startActivity(callIntent);
            }
        });

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //filter the listview while typing
//                arrayAdapter.getFilter().filter(s);
                hotlinesDisplayAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //filter the listview after typing
//                arrayAdapter.getFilter().filter(s);
                hotlinesDisplayAdapter.getFilter().filter(s);
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
                //get the first text that is before comma
                contactNames.add(row[0]);
                //get the second text that is after the comma
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

    private class HotlinesDisplayAdapter extends BaseAdapter implements Filterable {
        private final ArrayList<String> hotlineNames;
        private final ArrayList<String> hotlineContacts;
        private ArrayList<String> filteredNames;
        private ArrayList<String> filteredContacts;

        public HotlinesDisplayAdapter(ArrayList<String> names, ArrayList<String> contacts) {
            hotlineNames = names;
            hotlineContacts = contacts;
            filteredNames = names;
            filteredContacts = contacts;
        }

        @Override
        public int getCount() {
            return filteredNames.size();
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
                convertView = getLayoutInflater().inflate(R.layout.listview_item_hotlines, parent, false);
            }

            TextView txtHotlineName = convertView.findViewById(R.id.txt_hotlines_name);
            TextView txtHotlineContact = convertView.findViewById(R.id.txt_hotlines_contact);

            txtHotlineName.setText(filteredNames.get(position));
            txtHotlineContact.setText(String.format("#%s", filteredContacts.get(position)));

            return convertView;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    String searchString = charSequence.toString().toLowerCase();
                    if (searchString.isEmpty()) {
                        filteredNames = hotlineNames;
                        filteredContacts = hotlineContacts;
                    } else {
                        ArrayList<String> filteredList1 = new ArrayList<>();
                        ArrayList<String> filteredList2 = new ArrayList<>();
                        for (int i = 0; i < hotlineNames.size(); i++) {
                            String name = hotlineNames.get(i);
                            String contact = hotlineContacts.get(i);
                            if (name.toLowerCase().contains(searchString) || contact.toLowerCase().contains(searchString)) {
                                filteredList1.add(name);
                                filteredList2.add(contact);
                            }
                        }
                        filteredNames = filteredList1;
                        filteredContacts = filteredList2;
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = filteredNames;
                    filterResults.count = filteredNames.size();
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    filteredNames = (ArrayList<String>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }
    }
}