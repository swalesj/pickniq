package swalesj.pickniq.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
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

import swalesj.pickniq.Application.AppController;
import swalesj.pickniq.R;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;
import static swalesj.pickniq.Application.AppConfig.*;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private GoogleMap googleMap;
    private GoogleApiClient mGoogleApiClient;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private Location mLastKnownLocation;
    private boolean mLocationPermissionGranted = false;

    ArrayList<String> placeNames, placeDetails, placeLocations;
    private TextView location1name, location2name, location3name;
    private TextView location1details, location2details, location3details;
    private TextView searchText;
    private CardView card1, card2, card3;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        searchText = findViewById(R.id.subtext);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Searching...", Snackbar.LENGTH_SHORT)
                        .setAction("Search", null).show();
                googleMap.clear();
                try {
                    loadNearbyPlaces(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                } catch (NullPointerException n) {
                    Toast.makeText(getApplication().getBaseContext(), "No location data!",
                            Toast.LENGTH_LONG).show();
                }

                onMapReady(googleMap);
            }
        });

        location1name = findViewById(R.id.name1);
        location2name = findViewById(R.id.name2);
        location3name = findViewById(R.id.name3);

        location1details = findViewById(R.id.details1);
        location2details = findViewById(R.id.details2);
        location3details = findViewById(R.id.details3);

        card1 = findViewById(R.id.card1);
        card2 = findViewById(R.id.card2);
        card3 = findViewById(R.id.card3);

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
            Intent launch_friendsAndGroups = new Intent(this, FriendsGroupsActivity.class);
            startActivity(launch_friendsAndGroups);
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
        long radiusValue = 3;
        radiusValue = ((AppController) this.getApplication()).getUser().getPreferredRadius();

        LatLng latLng = new LatLng(lat, lon);
        int zoom = 13;
        if (radiusValue >= 5) zoom -= 1;
        if (radiusValue >= 10) zoom -= 1;
        if (radiusValue >= 20) zoom -= 1;

        boolean inexpensive = ((AppController) this.getApplication()).getUser().isInexpensive();
        boolean moderate = ((AppController) this.getApplication()).getUser().isModerate();
        boolean expensive = ((AppController) this.getApplication()).getUser().isExpensive();
        radiusValue *= 1609;
        String radius = Long.toString(radiusValue);
        StringBuilder placesURL =
                new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        placesURL.append("location=").append(lat).append(",").append(lon);
        placesURL.append("&radius=").append(radius);
        placesURL.append("&types=").append(type);
        placesURL.append("&sensor=true");
        placesURL.append("&key=" + "AIzaSyBQE86eAF8UylrcBwy7WQtJLDSUjQAaJLc");


        CameraPosition camPos = new CameraPosition.Builder()
                .target(latLng)
                .tilt(80)
                .zoom(zoom)
                .bearing(0)
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));

        if (((AppController) getApplication()).getUser().isOpennow()) {
            placesURL.append("&opennow=true");
        }
        // determine pricing
        if ((inexpensive && moderate && expensive) ||
                (inexpensive && !moderate && expensive)) {
            placesURL.append("&minprice=1");
            placesURL.append("&maxprice=4");
        }
        else if (inexpensive && moderate && !expensive) {
            placesURL.append("&minprice=1");
            placesURL.append("&maxprice=3");
        }
        else if ((!inexpensive && moderate && expensive)||
                    (!inexpensive && moderate && !expensive)) {
            placesURL.append("&minprice=2");
            placesURL.append("&maxprice=3");
        }
        else if (!inexpensive && !moderate && expensive) {
            placesURL.append("&minprice=3");
            placesURL.append("&maxprice=4");
        }
        else if (inexpensive && !moderate && !expensive) {
            placesURL.append("&minprice=1");
            placesURL.append("&maxprice=2");
        }
        else {
            placesURL.append("&minprice=0");
            placesURL.append("&maxprice=4");
        }


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

        String id, place_id, placeName = "", reference, icon, vicinity = null, placeAddress;
        double latitude = 0, longitude = 0;

        placeNames = new ArrayList<>();
        placeDetails = new ArrayList<>();
        placeLocations = new ArrayList<>();

        placeNames.clear();
        placeDetails.clear();
        placeLocations.clear();

        card1.setVisibility(View.GONE);
        card2.setVisibility(View.GONE);
        card3.setVisibility(View.GONE);

        searchText.setText("Showing the best restaurants according to your preferences:");

        try {
            JSONArray jsonArray = result.getJSONArray("results");

            if (result.getString(STATUS).equalsIgnoreCase(OK)) {

                String price_level;
                String price_display;
                String rating;
                String snippet;
                String currentAdd;
                double highestRating = 0;
                double secondHighestRating = 0;
                double thirdHighestRating = 0;
                //TODO Improve logic here; not displaying top 3


                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject place = jsonArray.getJSONObject(i);

                    //place_id = place.getString(PLACE_ID);
                    //places.add(GeoDataClient.getPlaceById(place_id));

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
                    Log.d(TAG, rating);
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

                    snippet = ("Rating: " + rating + "/5 " + "Price: " + price_display);
                    markerOptions.snippet(snippet);
                    currentAdd = Double.toString(latitude) + "," + Double.toString(longitude);

                    long minRating = ((AppController) this.getApplication()).getUser().getMinimumRating();
                    if (Double.parseDouble(rating) >= minRating) {
                        if (Double.parseDouble(rating) > highestRating) {
                            card1.setVisibility(View.VISIBLE);
                            placeDetails.add(0, snippet);
                            placeNames.add(0, placeName);
                            placeLocations.add(0, currentAdd);
                            if (snippet.matches("Rating: /5 Price: "));
                            {
                                //Toast.makeText(getApplication().getBaseContext(), "No results! Try a different search.",
                                //        Toast.LENGTH_LONG).show();
                            }
                            highestRating = Double.parseDouble(rating);
                        } else if (Double.parseDouble(rating) > secondHighestRating) {
                            card2.setVisibility(View.VISIBLE);
                            placeDetails.add(1, snippet);
                            placeNames.add(1, placeName);
                            placeLocations.add(1, currentAdd);
                            secondHighestRating = Double.parseDouble(rating);
                        } else if (Double.parseDouble(rating) > thirdHighestRating) {
                            card3.setVisibility(View.VISIBLE);
                            placeDetails.add(2, snippet);
                            placeNames.add(2, placeName);
                            placeLocations.add(2, currentAdd);
                            thirdHighestRating = Double.parseDouble(rating);
                        } else {
                            placeDetails.add(snippet);
                            placeNames.add(placeName);
                            placeLocations.add(currentAdd);
                        }
                        googleMap.addMarker(markerOptions).showInfoWindow();
                    }


                }
                if (placeNames.size() > 0) {
                    location1name.setText(placeNames.get(0));
                    location1details.setText(placeDetails.get(0));
                    card1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Uri mapUri = Uri.parse("http://maps.google.com/maps?q=" + placeLocations.get(0));
                            Intent intent = new Intent(Intent.ACTION_VIEW, mapUri);
                            intent.setPackage("com.google.android.apps.maps");
                            startActivity(intent);
                        }
                    });
                }
                if (placeNames.size() > 1) {
                    location2name.setText(placeNames.get(1));
                    location2details.setText(placeDetails.get(1));

                    card2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Uri mapUri = Uri.parse("http://maps.google.com/maps?q=" + placeLocations.get(1));
                            Intent intent = new Intent(Intent.ACTION_VIEW, mapUri);
                            intent.setPackage("com.google.android.apps.maps");
                            startActivity(intent);
                        }
                    });
                }
                if (placeNames.size() > 2) {
                    location3name.setText(placeNames.get(2));
                    location3details.setText(placeDetails.get(2));

                    card3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Uri mapUri = Uri.parse("http://maps.google.com/maps?q=" + placeLocations.get(2));
                            Intent intent = new Intent(Intent.ACTION_VIEW, mapUri);
                            intent.setPackage("com.google.android.apps.maps");
                            startActivity(intent);
                        }
                    });
                }
            }
            googleMap.getUiSettings().setZoomControlsEnabled(true);
        } catch (JSONException e) {

            e.printStackTrace();
            Log.e(TAG, "parseLocationResult: Error=" + e.getMessage());
        }
    }
}