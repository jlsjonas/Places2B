package desi.fluid.labs.places2b;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerDragListener {

    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 18234;
    private static final int TYPE_PLACE_TITLE = 0;
    private static final int TYPE_PLACE_DESC = 1;
    private static final int TYPE_CAT = 2;
    private static final int ACTION_PLACE_EDIT = 1;
    private static final int ACTION_CAT_EDIT = 2;
    private static final int ACTION_PLACE_DELETE = 3;
    private GoogleMap mMap;
    private View mapView;
    private List<Category> categories;
    private Map<Marker, Place> mPlace = new HashMap<>();
    private Menu menu;
    private Category activeCategory;
    private Place activePlace;
    private Marker activeMarker;

    private void updateCategories() {
        categories = Category.listAll(Category.class, "name");
        int i;
        menu.clear();
        menu.add(desi.fluid.labs.places2b.R.id.nav_group, desi.fluid.labs.places2b.R.id.nav_list, Menu.NONE, desi.fluid.labs.places2b.R.string.list_places);
        MenuItem mAll = menu.add(desi.fluid.labs.places2b.R.id.nav_categories, desi.fluid.labs.places2b.R.id.nav_all, Menu.NONE, desi.fluid.labs.places2b.R.string.show_all);
        mAll.setIcon(desi.fluid.labs.places2b.R.drawable.ic_all);
        MenuItem ci;
        Drawable ic = getDrawable(desi.fluid.labs.places2b.R.drawable.ic_bookmark_white);
        for (Category cat : categories) {
            ci = menu.add(desi.fluid.labs.places2b.R.id.nav_categories, cat.getId().intValue(), Menu.NONE, cat.name);
//            ic.setColorFilter(Color.HSVToColor(new float[]{237,.95f,.95f}), PorterDuff.Mode.MULTIPLY);
//            ci.setIcon(BitmapDescriptorFactory.defaultMarker(cat.color)); //ideal scenario
            ci.setIcon(ic);
        }
        //workaround androig bug https://code.google.com/p/android/issues/detail?id=191253
        menu.setGroupCheckable(desi.fluid.labs.places2b.R.id.nav_categories, true, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(desi.fluid.labs.places2b.R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(desi.fluid.labs.places2b.R.id.toolbar);
        mapView = findViewById(desi.fluid.labs.places2b.R.id.map);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(desi.fluid.labs.places2b.R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, desi.fluid.labs.places2b.R.string.navigation_drawer_open, desi.fluid.labs.places2b.R.string.navigation_drawer_close);
        assert drawer != null; //want anders is der iets mis :p
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(desi.fluid.labs.places2b.R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        menu = navigationView.getMenu();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(desi.fluid.labs.places2b.R.id.map);
        mapFragment.getMapAsync(this);
        updateCategories();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(desi.fluid.labs.places2b.R.id.drawer_layout);
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(desi.fluid.labs.places2b.R.menu.main, menu);
        return true;
    }

    public void setMarkerDescription() {
        setMarkerDescription(activeMarker, activePlace);
    }

    public void setMarkerDescription(Marker m, Place place) {
        if (place.description != null)
            m.setSnippet(place.description + ((place.category != null) ? "\nCategory: " + place.category : ""));
    }


    public Marker placeMarker(Place place) {

        Marker m = mMap.addMarker(new MarkerOptions()
                .draggable(true)
                .position(new LatLng(place.lat, place.lon))
                .title(place.name));
        setMarkerDescription(m, place);
        if (place.category != null)
            m.setIcon(BitmapDescriptorFactory.defaultMarker(place.category.color));
        mPlace.put(m, place);
        return m;

    }

    private void requestInfoFromUser(String title, String message, final int type) {

        final AlertDialog.Builder inputAlert = new AlertDialog.Builder(this);
        inputAlert.setTitle(title);
        inputAlert.setMessage(message);
        final EditText userInput = new EditText(this);
        if (type == TYPE_PLACE_DESC && activePlace != null)
            userInput.setText(activePlace.description);
        inputAlert.setView(userInput);
        inputAlert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String userInputValue = userInput.getText().toString();
                switch (type) {
                    case TYPE_PLACE_TITLE:
                        LatLng pos = mMap.getCameraPosition().target;
                        Place place = new Place(userInputValue, pos, activeCategory);
                        place.save();
                        placeMarker(place);
                        break;
                    case TYPE_PLACE_DESC:
                        activePlace.description = userInputValue;
                        activePlace.save();
                        setMarkerDescription();
                        break;
                    case TYPE_CAT:
                        Random rnd = new Random();
                        Category category = new Category(userInputValue, rnd.nextFloat() * 360);
                        category.save();
                        updateCategories();
                        break;
                    default:
                        infoTooltip("Unknown item", 0, Snackbar.LENGTH_SHORT);
                }
//                Toast.makeText(MainActivity.this, "got:" + userInputValue, Toast.LENGTH_SHORT).show();
            }
        });
        inputAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = inputAlert.create();
        alertDialog.show();
    }

    public void filterMarkers() {
        for (Map.Entry<Marker, Place> entry : mPlace.entrySet()) {
            Marker marker = entry.getKey();
            Place place = entry.getValue();
            if (activeCategory == null) {
                marker.setAlpha(1);
            } else if (place.category != null && activeCategory != null) {
                if (!Objects.equals(place.category.getId(), activeCategory.getId())) {
                    marker.hideInfoWindow();
                    marker.setAlpha(0);
                } else {
                    marker.setAlpha(1);
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == desi.fluid.labs.places2b.R.id.action_filter) {
            filterMarkers();
            return true;
        } else if (id == desi.fluid.labs.places2b.R.id.action_add) {
            requestInfoFromUser("title?", "Please enter a title", TYPE_PLACE_TITLE);
            return true;
        } else if (id == desi.fluid.labs.places2b.R.id.action_add_category) {
            requestInfoFromUser("Category name", "Please insert the requested category name", TYPE_CAT);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == desi.fluid.labs.places2b.R.id.nav_all) {
            activeCategory = null;
            filterMarkers();
        } else if (id == desi.fluid.labs.places2b.R.id.nav_list) {
            CplListFragment listFragment = new CplListFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(desi.fluid.labs.places2b.R.id.fragment_container, listFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        } else {
            activeCategory = Category.findById(Category.class, id);
            infoTooltip(activeCategory.getId() + activeCategory.name + " hue:" + ((int) activeCategory.color), ACTION_CAT_EDIT, Snackbar.LENGTH_LONG);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(desi.fluid.labs.places2b.R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    //noinspection MissingPermission
                    mMap.setMyLocationEnabled(true);
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(MainActivity.this, desi.fluid.labs.places2b.R.string.location_permission_toast, Toast.LENGTH_LONG).show();

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            mMap.setMyLocationEnabled(true);
        }
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//        mMap.setOnMapClickListener(this);
//        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerDragListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        // Add a marker in neighbourhood and move the camera
        mMap.getUiSettings().setZoomControlsEnabled(true);
        loadPlaces();
    }

    private void loadPlaces() {
        List<Place> places = Place.listAll(Place.class);
        for (Place place : places) {
            placeMarker(place);
        }
    }

    private void infoTooltip(String description, int action, int length) {
        Snackbar sb = Snackbar.make(mapView, description, length);
        switch (action) {
            case 0:
                break;
            case ACTION_PLACE_EDIT:
                sb.setAction("Edit", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestInfoFromUser("New description?", "", TYPE_PLACE_DESC);
                    }
                });
                break;
            case ACTION_PLACE_DELETE:
                sb.setAction("Remove", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activePlace.delete();
                        activeMarker.remove();
                        infoTooltip("Marker deleted", 0, Snackbar.LENGTH_SHORT);
                    }
                });
                break;
            default:
                infoTooltip("Not implemented", 0, Snackbar.LENGTH_INDEFINITE);
                break;
        }
        sb.show();
    }

    private void infoTooltip(String s) {
        infoTooltip(s, ACTION_PLACE_EDIT, Snackbar.LENGTH_INDEFINITE);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (mPlace.containsKey(marker)) {
            activeMarker = marker;
            activePlace = mPlace.get(activeMarker);
            infoTooltip(marker.getSnippet());
        } else {
            Snackbar.make(mapView, "Related item not found", Snackbar.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        infoTooltip(marker.getSnippet());
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        activeMarker = marker;
        activePlace = mPlace.get(activeMarker);
        Place place = mPlace.get(marker);
        place.setLatLon(marker.getPosition());
        place.save();
        infoTooltip("remove this marker?", ACTION_PLACE_DELETE, Snackbar.LENGTH_LONG);
    }
}
