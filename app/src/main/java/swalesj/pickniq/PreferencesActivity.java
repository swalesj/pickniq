package swalesj.pickniq;

import android.app.ActionBar;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.toIntExact;

public class PreferencesActivity extends AppCompatActivity {

    private static final String TAG = "preferencesActivity";

    private String UID;

    public HashMap<String, Object> data;
    public TextView radiusText, ratingText;
    public SeekBar minRating, radius;
    public CheckBox inexpensive, moderate, expensive, opennow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UID = ((AppController) this.getApplication()).getUID();
        setContentView(R.layout.activity_preferences);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initUIElements();
        getUserData();

        // Update Firestore data when fields are changed.
    }


    // Get user document snapshot from FireStore
    public void getUserData() {
        DocumentReference dRef;

        try {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            dRef = db.collection("Users").document(UID);
        } catch (Exception e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Error, cannot connect to database.");
            return;
        }

        dRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            public void onComplete(Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot dSnap = task.getResult();
                    if (dSnap != null) {
                        HashMap<String, Object> data = new HashMap<>(dSnap.getData());// task.getResult().getData();
                        setData(data);
                        Log.d(TAG, "DocumentSnapshot data: " + data);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void setData(HashMap map) {
        this.data = new HashMap<>(map);
        Log.d(TAG, "setData: " + this.data);

        // minimum_rating.
        long min = (long) data.get("minimum_rating");
        minRating.setProgress(toIntExact(min));

        // inexpensive.
        Boolean checked = (Boolean) data.get("inexpensive");
        Log.d(TAG, "setFieldsFromFirestore: " + checked);
        inexpensive.setChecked(checked);

        // moderate.
        checked = (Boolean) data.get("moderate");
        Log.d(TAG, "setFieldsFromFirestore: " + checked);
        moderate.setChecked(checked);

        // expensive.
        checked = (Boolean) data.get("expensive");
        Log.d(TAG, "setFieldsFromFirestore: " + checked);
        expensive.setChecked(checked);

        long radiusValue = (long) data.get("preferred_radius");
        radius.setProgress(toIntExact(radiusValue));

        checked = (Boolean) data.get("open_now");
        opennow.setChecked(checked);

    }

    public void updateFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users").document(UID).set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                      String success = "Successfully register user with Firestore DB.";
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                      String error = "Failed to register user with Firestore DB.";
            }
        });
    }


    // Initialize UI callbacks so that UI elements know what to do when they are changed.
    public void initUIElements() {
        // minimum rating.
        ratingText = findViewById(R.id.ratingText);
        minRating = findViewById(R.id.min_rating);

        minRating.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean b) {
                data.put("minimum_rating", value);
                String ratingLabel = "Minimum rating: " + String.valueOf(value);
                ratingText.setText(ratingLabel);
                updateFirestore();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // inexpensive.
        inexpensive = findViewById(R.id.inexpensive);
        inexpensive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!inexpensive.isChecked() && !moderate.isChecked() && !expensive.isChecked()) {
                    Toast.makeText(getApplication().getBaseContext(), "Please select at least one option!",
                            Toast.LENGTH_LONG).show();
                }
                data.put("inexpensive", inexpensive.isChecked());
                updateFirestore();
            }
        });

        // moderate.
        moderate = findViewById(R.id.moderate);
        moderate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!inexpensive.isChecked() && !moderate.isChecked() && !expensive.isChecked()) {
                    Toast.makeText(getApplication().getBaseContext(), "Please select at least one option!",
                            Toast.LENGTH_LONG).show();
                }
                data.put("moderate", moderate.isChecked());
                updateFirestore();
            }
        });

        // expensive.
        expensive = findViewById(R.id.expensive);
        expensive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!inexpensive.isChecked() && !moderate.isChecked() && !expensive.isChecked()) {
                    Toast.makeText(getApplication().getBaseContext(), "Please select at least one option!",
                            Toast.LENGTH_LONG).show();
                }
                data.put("expensive", expensive.isChecked());
                updateFirestore();
            }
        });

        // Open now.
        opennow = findViewById(R.id.open);
        opennow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data.put("open_now", opennow.isChecked());
                updateFirestore();
            }
        });

        // search radius.
        radiusText = findViewById(R.id.radiusText);
        radius = findViewById(R.id.radius_slider);

        radius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean b) {
                data.put("preferred_radius", value);
                String radiusLabel = "Search Radius: " + String.valueOf(value) + " miles";
                radiusText.setText(radiusLabel);
                updateFirestore();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

}
