package swalesj.pickniq;

import com.google.firebase.auth.FirebaseUser;

/**
 * Created by gabva on 2/19/2018.
 */
public class User {

    // Name.
    public String name;

    // Email.
    public String email;

    // Constructor
    public User(FirebaseUser u) {
        name = u.getDisplayName();
        email = u.getEmail();
    }


    // Get email.
    public String getEmail() {
        return email;
    }

    // Set email.
    public void setEmail(String email) {
        this.email = email;
    }

    // Get name.
    public String getName() {
        return name;
    }

    // Set name.
    public void setName(String name) {
        this.name = name;
    }

}
