package desi.fluid.labs.places2b;

import android.app.Activity;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by jonas on 20/05/16.
 */

public class ListAdapter extends BaseExpandableListAdapter {

    private final SparseArray<CategoryPlaceList> groups;
    public LayoutInflater inflater;
    public Activity activity;

    public ListAdapter(Activity act, SparseArray<CategoryPlaceList> groups) {
        activity = act;
        this.groups = groups;
        inflater = act.getLayoutInflater();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groups.get(groupPosition).children.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final Place children = (Place) getChild(groupPosition, childPosition);
        TextView text = null;
        if (convertView == null) {
            convertView = inflater.inflate(desi.fluid.labs.places2b.R.layout.place_item, null);
        }
        text = (TextView) convertView.findViewById(desi.fluid.labs.places2b.R.id.place_title);
        text.setText(children.name);
        text = (TextView) convertView.findViewById(desi.fluid.labs.places2b.R.id.place_desc);
        text.setText(children.description);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, children.name,
                        Toast.LENGTH_SHORT).show();
            }
        });
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                children.delete();
                //TODO: update corresponding markers/items
                Toast.makeText(activity, children.name + "removed, please refresh to reflect changes",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return groups.get(groupPosition).children.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(desi.fluid.labs.places2b.R.layout.place_group, null);
        }
        CategoryPlaceList group = (CategoryPlaceList) getGroup(groupPosition);
//        final CategoryPlaceList fgroup = (CategoryPlaceList) getGroup(groupPosition);
        ((CheckedTextView) convertView).setText(group.category.name);
        ((CheckedTextView) convertView).setChecked(isExpanded);
//        convertView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                for (Place place : fgroup.children) {
//                    place.delete();
//                }
//                fgroup.category.delete();
//                //TODO: update corresponding markers/items
//                Toast.makeText(activity, fgroup.category.name + " & children removed, please refresh to reflect changes",
//                        Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        });
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
