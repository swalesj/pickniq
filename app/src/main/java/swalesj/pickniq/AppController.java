package swalesj.pickniq;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseUser;

import static swalesj.pickniq.AppConfig.TAG;

/**
 * Created by swalesj
 */

public class AppController extends Application {

    private RequestQueue mRequestQueue;
    private static AppController mInstance;
    private String UID;
    private User u;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public String getUID() {
        return UID;
    }

    public User getUser() { return u; }

    public void setUID(String id) {
        this.UID = id;
    }

    public void setUser(User u) { this.u = u; }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
