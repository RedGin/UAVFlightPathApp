package com.edu.erau.uavflightpath;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import java.util.ArrayList;
import java.util.List;


public class waypointDisplay extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        String message = intent.getStringExtra(UAVFlightMap.EXTRA_MESSAGE);
        //parse the message

        String[] firstparse = message.split(";");
        String[] waypoints = new String[firstparse.length];
        List list = new ArrayList<String>();
        String[][] waypointinfo = new String[firstparse.length][4];
        System.out.println(firstparse);
        for(int i = 0; i<firstparse.length; i++){
            String[] secondparse = firstparse[i].split(":");
            waypoints[i] = secondparse[0];
            list.add(i,secondparse[0]);
            waypointinfo[i][0] = secondparse[1];
            waypointinfo[i][1] = secondparse[2];
            waypointinfo[i][2] = secondparse[3];
            waypointinfo[i][3] = secondparse[4];
            System.out.println(waypoints[i]);
        }

        Spinner spin = (Spinner) findViewById(R.id.waypointSpinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, list);
        spin.setAdapter(adapter);





    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_waypoint_display, menu);
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
