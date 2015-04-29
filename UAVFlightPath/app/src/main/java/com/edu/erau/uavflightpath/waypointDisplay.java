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
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


public class waypointDisplay extends Activity {

    int pos = 0; //default waypoint is 0
    ArrayList list;
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

        String message = intent.getStringExtra(UAVFlightMap.EXTRA_MESSAGE);
        //parse the message
        //string setup
        //waypointname;latitude:longitude:velocity:altitude;
        String[] firstparse = message.split(";");
        final String[] waypoints = new String[firstparse.length];

        spin = (Spinner) findViewById(R.id.waypointSpinner);
        list = new ArrayList();
        final String[][] waypointinfo = new String[firstparse.length][4];
        int i = 0;
        testing = "Waypoint #" + i;
        way = waypointHandler.findWaypoint(testing);
        while(i < waypointHandler.tableLength()){
            if (way != null) {
                list.add(i, way.get_waypointName());
            }
            testing = "Waypoint #" + i;
            way = waypointHandler.findWaypoint(testing);
            i++;

        }


        final EditText velocity = (EditText) findViewById(R.id.velocityText);
        final EditText altitude = (EditText) findViewById(R.id.altitudeText);
        final EditText latitude = (EditText) findViewById(R.id.latText);
        final EditText longitude = (EditText) findViewById(R.id.lonText);
        longitude.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        latitude.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        velocity.setInputType(InputType.TYPE_CLASS_NUMBER);
        altitude.setInputType(InputType.TYPE_CLASS_NUMBER);



        adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, list);
        //was having an issue where sometime the spinner being null so it would throw and exception
        if(spin != null) {
            spin.setAdapter(adapter);
        }
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

        Button save = (Button) findViewById(R.id.saveButton);
        Button cancel = (Button) findViewById(R.id.cancelButton);
        Button delete = (Button) findViewById(R.id.deleteButton);
        Button returntoMap = (Button) findViewById(R.id.returnButton);
        final Button export = (Button) findViewById(R.id.export);
        Button clearall = (Button) findViewById(R.id.clearPath);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tailcount>0) {
                    testing = "Waypoint #" + pos;
                    way = waypointHandler.findWaypoint(testing);
                    way.set_velocity(Integer.parseInt(velocity.getText().toString()));
                    way.set_altitude(Integer.parseInt(altitude.getText().toString()));
                    way.set_latitude(Double.parseDouble(latitude.getText().toString()));
                    way.set_longitude(Double.parseDouble(longitude.getText().toString()));
                    waypointHandler.update(way);
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tailcount>0) {
                    testing = "Waypoint #" + pos;
                    way = waypointHandler.findWaypoint(testing);
                    latitude.setText(Double.toString(way.get_latitude()));
                    longitude.setText(Double.toString(way.get_longitude()));
                    velocity.setText(Integer.toString(way.get_velocity()));
                    altitude.setText(Integer.toString(way.get_altitude()));
                }
            }
        });


        //deletes the selected waypoint and recreates the arrays
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testing = "Waypoint #" + pos;
                way = waypointHandler.findWaypoint(testing);
                 waypointHandler.deleteWaypoint(way.get_waypointName());
                //update the position of the arrays to account for the deleted waypoint

                //updates the spinner
                list.clear();
                int i = 0;
                testing = "Waypoint #" + i;
                way = waypointHandler.findWaypoint(testing);
                while(i < waypointHandler.tableLength()){
                    if (way != null) {
                        list.add(i, way.get_waypointName());
                    }
                    testing = "Waypoint #" + i;
                    way = waypointHandler.findWaypoint(testing);
                    i++;

                }

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
                 * heavy usage from
                 * http://paragchauhan2010.blogspot.com/2012/08/database-table-export-to-csv-in-android.html
                 * and
                 * http://viralpatel.net/blogs/java-read-write-csv-file/
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
