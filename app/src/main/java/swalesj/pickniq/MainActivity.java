package swalesj.pickniq;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;
import static swalesj.pickniq.AppConfig.*;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {


    private GoogleMap googleMap;
    private GoogleApiClient mGoogleApiClient;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION=1;
    private Location mLastKnownLocation;
    private boolean mLocationPermissionGranted = false;

    ArrayList<String> placeNames, placeDetails;
    private TextView location1name, location2name, location3name;
    private TextView location1details, location2details, location3details;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // TODO: Start newsearch selection when button is pressed
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View view) {
                Snackbar.make(view, "Searching...", Snackbar.LENGTH_SHORT)
                        .setAction("Search", null).show();
                googleMap.clear();
                loadNearbyPlaces(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                onMapReady(googleMap);
            }
        });

        location1name = findViewById (R.id.name1);
        location2name = findViewById (R.id.name2);
        location3name = findViewById (R.id.name3);

        location1details = findViewById(R.id.details1);
        location2details = findViewById(R.id.details2);
        location3details = findViewById(R.id.details3);

        getLocationPermission();
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }
    //DEPRECATED
    public void launchPrefDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String[] dialog_items_price = new String[]{
                "$",
                "$$",
                "$$$",
                ">5 miles",
                "Rating < 3",
                "Dine-in"
        };
        //these values will have to be pulled from Firebase later on
        boolean[] selected_items_price = new boolean[]{
                false, false, false, true, false, true
        };

        builder.setMultiChoiceItems(dialog_items_price, selected_items_price, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        //save checked options
                    }
                })
                .setIcon(R.mipmap.ic_launcher_round)
                .setNegativeButton(R.string.cancel_prefs, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface prefs, int id) {
                        //User wants to close the dialog
                        prefs.cancel();
                    }
                })
                .setPositiveButton(R.string.save_prefs, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface prefs, int id) {
                        //User wants to save preferences
                    }
                })

                .setTitle(R.string.prefs_title);
        AlertDialog prefs = builder.create();
        prefs.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        prefs.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            Intent mainActivity = new Intent(Intent.ACTION_MAIN);
            mainActivity.addCategory(Intent.CATEGORY_HOME);
            mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mainActivity);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sign_out) {
            FirebaseAuth.getInstance().signOut();
            Intent sign_out = new Intent(this, GoogleSignInActivity.class);
            startActivity(sign_out);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_groups) {
            // TODO Open group activity to modify user groups
        } else if (id == R.id.nav_favorites) {

        } else if (id == R.id.nav_prefs) {
            //launchPrefDialog();
            Intent launch_prefs = new Intent(this, PreferencesActivity.class);
            startActivity(launch_prefs);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        mLastKnownLocation = null;
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);

            getLocationPermission();
            try {
                    locationClient.getLastLocation()
                            .addOnSuccessListener(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    // GPS location can be null if GPS is switched off
                                    if (location != null) {
                                        MarkerOptions mOps = new MarkerOptions();
                                        mLastKnownLocation = location;
                                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                        mOps.position(latLng);
                                        mOps.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                        CameraPosition camPos = new CameraPosition.Builder()
                                                .target(latLng)
                                                .tilt(80)
                                                .zoom(13)
                                                .bearing(0)
                                                .build();
                                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
                                        googleMap.setMyLocationEnabled(true);
                                        //googleMap.addMarker(mOps);

                                        googleMap.setMapStyle(new MapStyleOptions(getResources()
                                                .getString(R.string.maps_style)));
                                        onLocationChanged(location);
                                        double lat = location.getLatitude();
                                        double lon = location.getLongitude();
                                        loadNearbyPlaces(lat, lon);
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("MapDemoActivity", "Error trying to get last GPS location");
                                    e.printStackTrace();
                                }
                            });
               // }
            } catch (SecurityException s) {
                //do something
            }

        }

    public void onLocationChanged(Location location) {
        // New location has now been determined
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        // You can now create a LatLng Object for use with maps
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    }

    private void getLocationPermission() {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    public void loadNearbyPlaces(double lat, double lon) {
        String type = "restaurant";
        String radius = "5000";
        StringBuilder placesURL =
                new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        placesURL.append("location=").append(lat).append(",").append(lon);
        placesURL.append("&radius=").append(radius);
        placesURL.append("&types=").append(type);
        placesURL.append("&sensor=true");
        placesURL.append("&key=" + "AIzaSyBQE86eAF8UylrcBwy7WQtJLDSUjQAaJLc");
        placesURL.append("&minprice=1");
        placesURL.append("&maxprice=4");

        JsonObjectRequest request = new JsonObjectRequest(placesURL.toString(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject result) {
                        //Log.i(TAG, "onResponse: Result= " + result.toString());
                        parseLocationResult(result);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        AppController.getInstance().addToRequestQueue(request);

    }
    private void parseLocationResult(JSONObject result) {

        String id, place_id, placeName = "", reference, icon, vicinity = null;
        double latitude, longitude;

        placeNames = new ArrayList<>();
        placeDetails = new ArrayList<>();
        placeNames.clear();
        placeDetails.clear();
        placeNames.removeAll(placeNames);
        placeDetails.removeAll(placeDetails);

        try {
            JSONArray jsonArray = result.getJSONArray("results");

            if (result.getString(STATUS).equalsIgnoreCase(OK)) {

                String price_level;
                String price_display;
                String rating;
                String snippet;
                double previousRating = 0;

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject place = jsonArray.getJSONObject(i);

                    id = place.getString(SUPERMARKET_ID);
                    place_id = place.getString(PLACE_ID);
                    if (!place.isNull(NAME)) {
                        placeName = place.getString(NAME);
                    }
                    if (!place.isNull(VICINITY)) {
                        vicinity = place.getString(VICINITY);
                    }
                    latitude = place.getJSONObject(GEOMETRY).getJSONObject(LOCATION)
                            .getDouble(LATITUDE);
                    longitude = place.getJSONObject(GEOMETRY).getJSONObject(LOCATION)
                            .getDouble(LONGITUDE);
                    reference = place.getString(REFERENCE);
                    price_level = place.getString(PRICE_LEVEL);
                    rating = place.getString(RATING);
                    MarkerOptions markerOptions = new MarkerOptions();
                    LatLng latLng = new LatLng(latitude, longitude);
                    markerOptions.position(latLng);
                    markerOptions.title(placeName);

                    switch (price_level) {
                        case "1":
                            price_display = "$";
                            break;
                        case "2":
                            price_display = "$$";
                            break;
                        case "3":
                            price_display = "$$$";
                            break;
                        case "4":
                            price_display = "$$$$";
                            break;
                        default:
                            price_display = "No data";
                            break;
                    }

                    snippet = ("Rating: "+rating+"/5 "+"Price: "+price_display);
                    markerOptions.snippet(snippet);

                    if (Double.parseDouble(rating) >= previousRating) {
                        placeDetails.add(0,snippet);
                        placeNames.add(0,placeName);
                        previousRating = Double.parseDouble(rating);
                    }
                    else {
                        placeDetails.add(snippet);
                        placeNames.add(placeName);
                    }

                    googleMap.addMarker(markerOptions).showInfoWindow();
                }
                if (placeNames.size() > 0) {
                    location1name.setText(placeNames.get(0));
                    location1details.setText(placeDetails.get(0));
                }
                if (placeNames.size() > 1) {
                    location2name.setText(placeNames.get(1));
                    location2details.setText(placeDetails.get(1));
                }
                if (placeNames.size() > 2) {
                    location3name.setText(placeNames.get(2));
                    location3details.setText(placeDetails.get(2));
                }
            }
            googleMap.getUiSettings().setZoomControlsEnabled(true);
        } catch (JSONException e) {

            e.printStackTrace();
            Log.e(TAG, "parseLocationResult: Error=" + e.getMessage());
        }
    }
}
