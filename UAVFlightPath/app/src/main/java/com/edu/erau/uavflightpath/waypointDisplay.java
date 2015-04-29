package com.edu.erau.uavflightpath;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import java.util.ArrayList;


public class waypointDisplay extends Activity {

    int pos = 0; //default waypoint is 0
    ArrayList list;
    Spinner spin;
    ArrayAdapter<String> adapter;
    int tailcount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waypoint_display);
        waypointDB waypointHandler = new waypointDB(waypointDisplay.this, null, null, 1);
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

        for(int i = 0; i<firstparse.length; i++){
            String[] secondparse = firstparse[i].split(":");
            waypoints[i] = secondparse[0];
            list.add(i,waypoints[i]);
            waypointinfo[i][0] = secondparse[1];
            waypointinfo[i][1] = secondparse[2];
            waypointinfo[i][2] = secondparse[3];
            waypointinfo[i][3] = secondparse[4];
        }
        tailcount = waypoints.length;

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
                latitude.setText(waypointinfo[position][0]);
                longitude.setText(waypointinfo[position][1]);
                velocity.setText(waypointinfo[position][2]);
                altitude.setText(waypointinfo[position][3]);
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

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tailcount>0) {
                    waypointinfo[pos][2] = velocity.getText().toString();
                    waypointinfo[pos][3] = altitude.getText().toString();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tailcount>0) {
                    velocity.setText(waypointinfo[pos][2].toString());
                    altitude.setText(waypointinfo[pos][3].toString());
                }
            }
        });


        //deletes the selected waypoint and recreates the arrays
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //deletes the selected waypoint by setting the variables to null


                //update the position of the arrays to account for the deleted waypoint
                if (tailcount > 0) {
                    for (int i = pos; i < waypoints.length - 1; i++) {
                        waypoints[i] = "Waypoint #" + (i);
                        waypointinfo[i][0] = waypointinfo[i + 1][0];
                        waypointinfo[i][1] = waypointinfo[i + 1][1];
                        waypointinfo[i][2] = waypointinfo[i + 1][2];
                        waypointinfo[i][3] = waypointinfo[i + 1][3];
                    }
                    tailcount = tailcount - 1;
                    waypoints[tailcount] = null;
                    waypointinfo[tailcount][0] = null;
                    waypointinfo[tailcount][1] = null;
                    waypointinfo[tailcount][2] = null;
                    waypointinfo[tailcount][3] = null;

                    //updates the spinner
                    list.clear();
                    for (int i = 0; i < tailcount; i++) {
                        list.add(i, waypoints[i]);
                    }

                    adapter.notifyDataSetChanged();
                    latitude.setText(waypointinfo[pos][0]);
                    longitude.setText(waypointinfo[pos][1]);
                    velocity.setText(waypointinfo[pos][2]);
                    altitude.setText(waypointinfo[pos][3]);
                }
            }
        });

        returntoMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
