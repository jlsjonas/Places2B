package desi.fluid.labs.places2b;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.List;

/**
 * Created by jonas on 20/05/16.
 */
public class CplListFragment extends Fragment {
    // more efficient than HashMap for mapping integers to objects
    SparseArray<CategoryPlaceList> groups = new SparseArray<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.place_list, container, false);
        ExpandableListView listView = (ExpandableListView) inflater.inflate(desi.fluid.labs.places2b.R.layout.place_list, container, false);
        ListAdapter adapter = new ListAdapter(getActivity(),
                groups);
        listView.setAdapter(adapter);
        return listView;
    }

    public void createData() {
        List<Category> categories = Category.listAll(Category.class, "name");
        int i = 0;
        for (Category cat : categories) {
            CategoryPlaceList cpl = new CategoryPlaceList(cat);
            groups.append(i, cpl);
            i++;
        }
//        for (int j = 0; j < 5; j++) {
//            CategoryPlaceList group = new CategoryPlaceList("Test " + j);
//            for (int i = 0; i < 5; i++) {
//                group.children.add("Sub Item" + i);
//            }
//            groups.append(j, group);
//        }
    }
}
