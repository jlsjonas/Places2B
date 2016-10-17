package desi.fluid.labs.places2b;

import java.util.List;

/**
 * Created by jonas on 20/05/16.
 */
public class CategoryPlaceList {
    public final List<Place> children;
    public Category category;

    public CategoryPlaceList(Category category) {
        this.category = category;
        this.children = category.getPlaces();
    }
}
