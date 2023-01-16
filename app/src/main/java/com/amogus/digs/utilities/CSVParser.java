package com.amogus.digs.utilities;

import android.content.Context;
import com.amogus.digs.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CSVParser {
    private final Context context;
    private final int resource;
    private int rowCount;
    private final String splitter;
    private final ArrayList<ArrayList<String>> listOfArrayList = new ArrayList<>();;

    public CSVParser(Context context, int resource, String splitter) {
        this.context = context;
        this.resource = resource;
        this.splitter = splitter;

        setRowCount();
        for (int x = 0; x < getRowCount(); x++) {
            listOfArrayList.add(new ArrayList<>());
        }
        setListOfArrayList();
    }

    private void setRowCount() {
        try (InputStream inputStream = context.getResources().openRawResource(resource) ) {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                String csvLine;
                while ((csvLine = bufferedReader.readLine()) != null) {
                    String[] row = csvLine.split(splitter);
                    rowCount = row.length;
                }
            } catch (IOException e) {
                throw new RuntimeException("Error in reading CSV file: " + e);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error while closing input stream: " + e);
        }
    }

    private void setListOfArrayList() {
        try (InputStream inputStream = context.getResources().openRawResource(resource) ) {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                String csvLine;
                while ((csvLine = bufferedReader.readLine()) != null) {
                    String[] row = csvLine.split(splitter);
                    for (int x = 0; x < row.length; x++) {
                        listOfArrayList.get(x).add(row[x]);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public int getRowCount() {
        return rowCount;
    }

    public ArrayList<String> getArrayList(int row) {
        return listOfArrayList.get(row);
    }
}
