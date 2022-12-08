package com.amogus.digs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.fragment.app.Fragment;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.ScrollHandle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

//this is the java fragment for wiki fragment
public class WikiFragment extends Fragment {
    private String TAG = "WikiFragment";
    private PDFView pdfView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //once this java fragment is called, it will display the wiki fragment to the fragment container
        //which is the frame layout
        View view = inflater.inflate(R.layout.fragment_wiki, container, false);

        ArrayList<String> list = new ArrayList<String>();
        String[] arr;
        try {
            //get all the file names inside the assets/wiki folder
            arr = getActivity().getAssets().list("wiki");
            for (String file : arr) {
                list.add(file.replace(".pdf", ""));
            }
        } catch (IOException e) {
            Log.e(TAG, "Error, no files found in selected directory: ", e);
        }

        Spinner spinner = view.findViewById(R.id.spinner);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        //set the item selected everytime the fragment is opened
        spinner.setSelection(list.indexOf("mema"));

        pdfView = view.findViewById(R.id.pdf_viewer);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = parent.getItemAtPosition(position).toString();
                String pdfPath = "wiki/" + text + ".pdf";
                pdfView.fromAsset(pdfPath)
                        .pages(0, 2, 1, 3, 3, 3) // all pages are displayed by default
                        .enableSwipe(true) // allows to block changing pages using swipe
                        .swipeHorizontal(false)
                        .enableDoubletap(true)
                        .defaultPage(0)
                        .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
                        .password(null)
                        .scrollHandle(null)
                        .enableAntialiasing(true) // improve rendering a little bit on low-res screens
                        .spacing(0) // spacing between pages in dp. To define spacing color, set view background
                        .autoSpacing(false) // add dynamic spacing to fit each page on its own on the screen
                        .fitEachPage(true) // fit each page to the view, else smaller pages are scaled relative to largest page.
                        .pageSnap(true) // snap pages to screen boundaries
                        .pageFling(false) // make a fling change only a single page like ViewPager
                        .nightMode(false) // toggle night mode
                        .load();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }
}