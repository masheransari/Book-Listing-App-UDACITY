package com.example.asheransari.booklistinggoogle;

/**
 * Created by asher.ansari on 9/23/2016.
 */
public class Customclass {

    private String magnitude;

    private String place;

    private long time;

    private String mUrl;



    public Customclass(String place, String magnitude1){
        this.magnitude = magnitude1;
        this.place = place;
//        this.time = time;
//        this.mUrl = url;
    }

    public String getmUrl() {
        return mUrl;
    }

    public String getMagnitude() {
        return magnitude;
    }

    public String getPlace() {
        return place;
    }

    public long getTime() {
        return time;
    }






}
