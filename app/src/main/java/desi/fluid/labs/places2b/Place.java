package desi.fluid.labs.places2b;

import com.google.android.gms.maps.model.LatLng;
import com.orm.SugarRecord;

/**
 * Created by Jolien on 18/05/2016.
 */
public class Place extends SugarRecord {
    String name;
    String description;
    Double lat;
    Double lon;

    Category category;

    public Place() {
    }

    public Place(LatLng latLng) {
        this.lat = latLng.latitude;
        this.lon = latLng.longitude;
    }

    public Place(String name) {
        this.name = name;
    }

    public Place(String name, LatLng latLng) {
        this.name = name;
        this.lat = latLng.latitude;
        this.lon = latLng.longitude;
    }

    public Place(LatLng latLng, Category category) {
        this.lat = latLng.latitude;
        this.lon = latLng.longitude;
        this.category = category;
    }

    public Place(String name, LatLng latLng, Category category) {
        this.name = name;
        this.lat = latLng.latitude;
        this.lon = latLng.longitude;
        this.category = category;
    }

    public Place(String name, LatLng latLng, String description, Category category) {
        this.name = name;
        this.lat = latLng.latitude;
        this.lon = latLng.longitude;
        this.description = description;
        this.category = category;
    }

    public void setLatLon(LatLng latLng) {
        this.lat = latLng.latitude;
        this.lon = latLng.longitude;
    }

}
