package com.adilson.wetalk;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity {
    private String TAG = getClass().getSimpleName();

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private User currentUser;
    private DatabaseReference databaseReference;
    private ProgressDialog dialog;
    private MainFragmentAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));


        searchCurrentUser();

        tabLayout = findViewById(R.id.main_tabLayout);
        viewPager = findViewById(R.id.main_view_pager);

        tabLayout.setupWithViewPager(viewPager);


    }

    private User convertToUser(DataSnapshot dataSnapshot) {
        try {
            User value = dataSnapshot.getValue(User.class);
            value.setUserId(dataSnapshot.getKey());
            return value;
        } catch (Exception ex) {
            Log.e(TAG, "convertToUser: ", ex);
            return null;
        }

    }

    private void searchCurrentUser() {
        dialog = new ProgressDialog(this);
        dialog.show();

        databaseReference = FirebaseDatabase.getInstance().getReference(DatabaseHelper.USERS);
        databaseReference
                .child(FirebaseAuth.getInstance().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try {
                            Log.d(TAG, "onDataChange: " + dataSnapshot.getValue());
                            currentUser = convertToUser(dataSnapshot);

                            showLayout(currentUser);


                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        } catch (ClassCastException e) {
                            e.printStackTrace();
                        } finally {
                            dialog.dismiss();
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "onCancelled: ", databaseError.toException());
                        dialog.dismiss();
                    }
                });


    }

    private void showLayout(User currentUser) {
        pagerAdapter = new MainFragmentAdapter(this.getSupportFragmentManager(), currentUser);
        viewPager.setAdapter(pagerAdapter);
    }


    public static class MainFragmentAdapter extends FragmentPagerAdapter {

        private final User currentUser;

        public MainFragmentAdapter(FragmentManager fm, User currentUser) {
            super(fm);
            this.currentUser = currentUser;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return UserListFragment.cleanUserListInstance(currentUser);
                case 1:
                    return ChatListFragment.newInstance(currentUser);
                case 2:
                    return UserDataFragment.newInstance(currentUser);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return "Usuarios";
                case 1:
                    return "Conversas";
                case 2:
                    return "Perfil";
                default:
                    return "";
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_screen_menu, menu);

        menu.findItem(R.id.main_menu_sign_out).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                return true;
            }
        });

        return true;
    }
}
