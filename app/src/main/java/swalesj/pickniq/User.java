package swalesj.pickniq;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/*
 */
public class User {
    // Debug Tag.
    private static final String TAG = "User";

    // Unique ID.
    private String uid;

    // Name.
    private String name;

    // Email.
    private String email;

    // Constructor.
    public User(FirebaseUser u) {
        uid = u.getUid();
        name = u.getDisplayName();
        email = u.getEmail();
    }


    // Get email.
    public String getEmail() { return email; }

    // Set email.
    public void setEmail(String email) { this.email = email;
    }

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

    // Register.
    public void register() {
        // TODO: Should we check to see if we already exist before proceeding to register?

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", getName());
        userData.put("email", getEmail());
//        userData.put("phoneNumber", getPhoneNumber());
        String uid = getUid();

        Log.d(TAG, "HERE");

        db.collection("Users").document(uid).set(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                        String success = "Successfully register user with Firestore DB.";
//                        Log.d(TAG, success);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                String error = "Failed to register user with Firestore DB.";
//                System.out.println("");
            }
        });
    }
}
