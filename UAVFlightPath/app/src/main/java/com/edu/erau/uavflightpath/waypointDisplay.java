package com.edu.erau.uavflightpath;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

/**
 * Created by Michael Philotoff:
 *
 * Copyright [2015] [opencsv.sourceforge.net]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class waypointDisplay extends Activity {

    int pos = 0; //default waypoint is 0
    ArrayList list = new ArrayList();
    Spinner spin;
    ArrayAdapter<String> adapter;
    waypointDB waypointHandler;
    int tailcount = 0;
    waypoint way;
    String testing;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waypoint_display);
        waypointHandler = new waypointDB(waypointDisplay.this, null, null, 3);
        Intent intent = getIntent();
        spin = (Spinner) findViewById(R.id.waypointSpinner);

        /*
        I started off with passing in a string of data and parsing it into usable waypoints and waypoint information
        String message = intent.getStringExtra(UAVFlightMap.EXTRA_MESSAGE);
        //parse the message
        //string setup
        //waypointname;latitude:longitude:velocity:altitude;
        String[] firstparse = message.split(";");
        final String[] waypoints = new String[firstparse.length]

        final String[][] waypointinfo = new String[firstparse.length][4];
        */

        //setup to populate the list of waypoints
        int i = 0;
        testing = "Waypoint #" + i;
        way = waypointHandler.findWaypoint(testing);

        //goes through the full table length and if the row is not null it adds it to the list to be put into the spinner
        while(way != null){

            list.add(i, way.get_waypointName());

            testing = "Waypoint #" + i;
            way = waypointHandler.findWaypoint(testing);
            i++;

        }

        //setup for the edit text areas for latitude, longitude, volocity, and altitude.
        final EditText velocity = (EditText) findViewById(R.id.velocityText);
        final EditText altitude = (EditText) findViewById(R.id.altitudeText);
        final EditText latitude = (EditText) findViewById(R.id.latText);
        final EditText longitude = (EditText) findViewById(R.id.lonText);

        //limits what the input values can be
        longitude.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        latitude.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        velocity.setInputType(InputType.TYPE_CLASS_NUMBER);
        altitude.setInputType(InputType.TYPE_CLASS_NUMBER);


        //adds the list array to an arrayadapter to populate the spinner
        adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, list);

        //was having an issue where sometime the spinner being null so it would throw and exception
        if(spin != null) {
            spin.setAdapter(adapter);
        }

        //once an item is selected it populates the latitude, longitude, velocity, and altitude areas by graping data from the database
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                testing = "Waypoint #" + position;
                way = waypointHandler.findWaypoint(testing);
                latitude.setText(Double.toString(way.get_latitude()));
                longitude.setText(Double.toString(way.get_longitude()));
                velocity.setText(Integer.toString(way.get_velocity()));
                altitude.setText(Integer.toString(way.get_altitude()));
                pos = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //setup for buttons
        Button save = (Button) findViewById(R.id.saveButton);
        Button cancel = (Button) findViewById(R.id.cancelButton);
        Button delete = (Button) findViewById(R.id.deleteButton);
        Button returntoMap = (Button) findViewById(R.id.returnButton);
        final Button export = (Button) findViewById(R.id.export);
        Button clearall = (Button) findViewById(R.id.clearPath);

        //update the fields for
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    testing = "Waypoint #" + pos;
                    way = waypointHandler.findWaypoint(testing);
                    way.set_velocity(Integer.parseInt(velocity.getText().toString()));
                    way.set_altitude(Integer.parseInt(altitude.getText().toString()));
                    way.set_latitude(Double.parseDouble(latitude.getText().toString()));
                    way.set_longitude(Double.parseDouble(longitude.getText().toString()));
                    waypointHandler.update(way);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    testing = "Waypoint #" + pos;
                    way = waypointHandler.findWaypoint(testing);
                    latitude.setText(Double.toString(way.get_latitude()));
                    longitude.setText(Double.toString(way.get_longitude()));
                    velocity.setText(Integer.toString(way.get_velocity()));
                    altitude.setText(Integer.toString(way.get_altitude()));

            }
        });


        //deletes the selected waypoint and recreates the arrays
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testing = "Waypoint #" + pos;
                way = waypointHandler.findWaypoint(testing);
                 waypointHandler.deleteWaypoint(way.get_waypointName());

                //removes everything from the list to be updated with new values
                list.clear();

                //added in waypoints to the list to be added into the spinner
                int i = 0;
                testing = "Waypoint #" + i;
                way = waypointHandler.findWaypoint(testing);

                //
                while(way != null){

                        list.add(i,way.get_waypointName());

                    //grabs the next row in the database
                    testing = "Waypoint #" + i;
                    way = waypointHandler.findWaypoint(testing);
                    i++;

                }

                 //update the spinner
                 adapter.notifyDataSetChanged();

                }

        });

        returntoMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        clearall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waypointHandler.dropTable();

            }
        });

        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * exporting as a csv file
                 * borrowed heavily from
                 * http://paragchauhan2010.blogspot.com/2012/08/database-table-export-to-csv-in-android.html
                 * and
                 * http://viralpatel.net/blogs/java-read-write-csv-file/
                 * to implement exporting a csv file
                 */
                //accesses external storage
                File csv = new File(Environment.getExternalStorageDirectory(),"");


                //check if the directory exist or not and if not creates one
                if (!csv.exists()){
                    csv.mkdirs();
                }

                //specified file name
                File file = new File( csv, "UAVPath.csv");
                try{
                    //
                    CSVWriter writer = new CSVWriter(new FileWriter(file));
                    SQLiteDatabase db = waypointHandler.getReadableDatabase();
                    Cursor curCSV = db.rawQuery("SELECT * FROM waypoint",null);
                    writer.writeNext(curCSV.getColumnNames());
                    while(curCSV.moveToNext()){
                        String arrString[] = {curCSV.getString(0),curCSV.getString(1),curCSV.getString(2),curCSV.getString(3),curCSV.getString(4)};
                        writer.writeNext(arrString);
                    }
                    writer.close();
                    curCSV.close();
                }catch(Exception e) {

                }

            }
        });
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
