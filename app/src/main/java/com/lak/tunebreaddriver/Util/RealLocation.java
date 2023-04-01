package com.lak.tunebreaddriver.Util;
//package com.example.userapp.Util;

import com.google.android.gms.maps.model.LatLng;

public class RealLocation {
    private  LatLng position;
    private  String title;

    public RealLocation() {
    }
    public RealLocation(LatLng position, String title) {
        this.position = position;
        this.title = title;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LatLng getPosition() {
        return position;
    }

    public String getTitle() {
        return title;
    }
}
