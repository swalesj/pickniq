package swalesj.pickniq.Data_Classes;

import android.app.Application;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.api.core;

import java.util.HashMap;
import java.util.Map;

/*
 * User.
 */
public class User {
    // Debug tag.
    private static final String TAG = "User";

    // User exists?
    private boolean registered;

    // Unique ID.
    private String uid;

    // Name.
    private String name;

    // Email.
    private String email;

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }

    public long getPreferredRadius() {
        return preferredRadius;
    }

    public void setPreferredRadius(long preferredRadius) {
        this.preferredRadius = preferredRadius;
    }

    public long getMinimumRating() {
        return minimumRating;
    }

    public void setMinimumRating(long minimumRating) {
        this.minimumRating = minimumRating;
    }

    public boolean isInexpensive() {
        return inexpensive;
    }

    public void setInexpensive(boolean inexpensive) {
        this.inexpensive = inexpensive;
    }

    public boolean isModerate() {
        return moderate;
    }

    public void setModerate(boolean moderate) {
        this.moderate = moderate;
    }

    public boolean isExpensive() {
        return expensive;
    }

    public void setExpensive(boolean expensive) {
        this.expensive = expensive;
    }

    public boolean isOpennow() {
        return opennow;
    }

    public void setOpennow(boolean opennow) {
        this.opennow = opennow;
    }

    private long preferredRadius, minimumRating;
    private boolean inexpensive, moderate, expensive, opennow;

    // Constructor.
    public User(FirebaseUser u) {
        uid = u.getUid();
        name = u.getDisplayName();
        email = u.getEmail();
        minimumRating = 1;
        preferredRadius = 3;
        // Below lines were an idea for allowing user object to register itself to Firestore DB..
        // Doesn't grab data fast enough.
        // This may require some app-wide utilities for getting data and waiting for said data.
        // registered = checkUserExists();
        // if (!isRegistered()) register();
    }


    // Get email.
    public String getEmail() { return email; }

    // Set email.
    public void setEmail(String email) { this.email = email; }

    // Get name.
    public String getName() { return name; }

    // Set name.
    public void setName(String name) { this.name = name; }

    // Get unique ID.
    public String getUid() {
        return uid;
    }

    // Set unique ID.
    public void setUid(String uid) {
        this.uid = uid;
    }

    // Is registered to Firestore?
    public boolean isRegistered() { return registered; }

    // Register user to Firestore.
    public void register() {
        setDefaultPrefs();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = getUid();
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", getName());
        userData.put("email", getEmail());
        userData.put("preferred_radius", this.preferredRadius);
        userData.put("minimum_rating", this.minimumRating);
        userData.put("inexpensive", this.inexpensive);
        userData.put("moderate", this.moderate);
        userData.put("expensive", this.expensive);
        userData.put("open_now", this.opennow);

        db.collection("Users").document(uid).set(userData)
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

    // Check if user exists in Firestore DB.
    // Update 'registered' field if necessary.
    public boolean checkUserExists() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dRef = db.collection("Users").document(uid);
        dRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            public void onComplete(Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        registered = true;
                        Log.d(TAG, "DocumentSnapshot data: " + task.getResult().getData());
                    } else {
                        registered = false;
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        return isRegistered();
    }

    // Set default preferences.
    public void setDefaultPrefs() {
        this.preferredRadius = 10;
        this.minimumRating = 1;
        this.inexpensive = true;
        this.moderate = true;
        this.expensive = true;
        this.opennow = false;
    }
}
