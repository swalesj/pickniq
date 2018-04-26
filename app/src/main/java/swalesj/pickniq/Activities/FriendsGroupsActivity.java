package swalesj.pickniq.Activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import android.app.FragmentManager;
import android.app.Fragment;
import swalesj.pickniq.Fragments.FriendsFragment;
import swalesj.pickniq.Fragments.GroupsFragment;
import swalesj.pickniq.R;

public class FriendsGroupsActivity extends AppCompatActivity implements
        FriendsFragment.OnFragmentInteractionListener,
        GroupsFragment.OnFragmentInteractionListener {

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    switchToFragmentFriends();
                    return true;
                case R.id.navigation_notifications:
                    switchToFragmentGroups();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_groups);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        switchToFragmentFriends();
    }

    // Switch to friends fragment.
    public void switchToFragmentFriends() {
        FragmentManager manager = getFragmentManager();
        manager.beginTransaction().replace(R.id.container, new FriendsFragment()).commit();
    }

    // Switch to groups fragment.
    public void switchToFragmentGroups() {
        FragmentManager manager = getFragmentManager();
        manager.beginTransaction().replace(R.id.container, new GroupsFragment()).commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri){
        // Must exist. Can be left empty.
    }
}
