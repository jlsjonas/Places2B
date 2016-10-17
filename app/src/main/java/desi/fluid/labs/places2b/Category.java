package desi.fluid.labs.places2b;

import com.orm.SugarRecord;

import java.util.List;

/**
 * Created by Jolien on 18/05/2016.
 */
public class Category extends SugarRecord {
    String name;
    float color;

    public Category() {
    }

    public Category(String name) {
        this.name = name;
    }

    public Category(String name, float color) {
        this.name = name;
        this.color = color;
    }

    List<Place> getPlaces() {
        return find(Place.class, "category = ?", getId().toString());
    }

    @Override
    public String toString() {
        return name;
    }
}
