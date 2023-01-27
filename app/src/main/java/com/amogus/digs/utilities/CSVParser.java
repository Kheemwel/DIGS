package com.amogus.digs.utilities;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

//this class is made to parse the csv file
public class CSVParser {
    private final Context context;
    private final int resource;
    private int columnCount;
    private final String splitter;
    private final ArrayList<ArrayList<String>> listOfColumn; //arraylist that holds a list of arraylist that holds a list of string values

    //this constructor can be instantiated and the class fields is different for every instance
    //the resource is an id of the location of the csv file which is located in res/raw folder
    //the splitter is the character that divides the data inside the csv file, it's usually a comma (,)
    public CSVParser(Context context, int resource, String splitter) {
        this.context = context;
        this.resource = resource;
        this.splitter = splitter;

        listOfColumn = new ArrayList<>(); //create a new listOfColumn arraylist
        setColumnCount(); //set the column count based on the number of columns inside the csv file
        for (int x = 0; x < getColumnCount(); x++) {
            listOfColumn.add(new ArrayList<>()); //add new arraylist depends on the number of columns
        }
        setColumnDataList(); //adds the information to the arraylist
    }

    private void setColumnCount() {
        //this try block is equivalent to using block in C#, it automatically opens and closes the used resource
        try (InputStream inputStream = context.getResources().openRawResource(resource) ) {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                String csvLine;
                while ((csvLine = bufferedReader.readLine()) != null) { //get the information in every line and put it into the csvline variable
                    String[] row = csvLine.split(splitter); //split the string based on the splitter and make it as array
                    columnCount = row.length; //the column count is now based on the number of column which is the number of elements inside the array
                }
            } catch (IOException e) {
                throw new RuntimeException("Error in reading CSV file: " + e);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error while closing input stream: " + e);
        }
    }

    private void setColumnDataList() {
        try (InputStream inputStream = context.getResources().openRawResource(resource) ) {
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                String csvLine;
                while ((csvLine = bufferedReader.readLine()) != null) {
                    String[] row = csvLine.split(splitter);
                    for (int x = 0; x < row.length; x++) {
                        listOfColumn.get(x).add(row[x]); //get the arraylist based on the index, so the first element of array will be added inside the first arraylist and so on
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Error in reading CSV file: " + e);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error while closing input stream: " + e);
        }
    }


    //return the number of column
    public int getColumnCount() {
        return columnCount;
    }

    //return the ArrayList of ColumnDataList
    public ArrayList<String> getColumnDataList(int column) {
        return listOfColumn.get(column);
    }
}
