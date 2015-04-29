package com.edu.erau.uavflightpath;

/**
 * Created by Michael on 4/28/2015.
 */
public class waypoint {

    private String _waypointName;
    private double _latitude;
    private double _longitude;
    private int _altitude;
    private int _velocity;

    public waypoint() {

    }

    public waypoint(String waypointName, double latitude, double longitude, int altitude, int velocity){
        this._latitude = latitude;
        this._waypointName = waypointName;
        this._longitude = longitude;
        this._altitude = altitude;
        this._velocity = velocity;
    }

    public String get_waypointName() {
        return this._waypointName;
    }

    public void set_waypointName(String _waypointName) {
        this._waypointName = _waypointName;
    }

    public double get_latitude() {
        return this._latitude;
    }

    public void set_latitude(double _latitude) {
        this._latitude = _latitude;
    }

    public double get_longitude() {
        return this._longitude;
    }

    public void set_longitude(double _longitude) {
        this._longitude = _longitude;
    }

    public int get_altitude() {
        return this._altitude;
    }

    public void set_altitude(int _altitude) {
        this._altitude = _altitude;
    }

    public int get_velocity() {
        return this._velocity;
    }

    public void set_velocity(int _velocity) {
        this._velocity = _velocity;
    }
}
