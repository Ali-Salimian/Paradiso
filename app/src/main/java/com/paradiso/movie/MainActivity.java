package com.paradiso.movie;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.paradiso.movie.follower.PreFollowerSearchFragment;
import com.paradiso.movie.home.HomeFragment;
import com.paradiso.movie.profile.ProfileFragment;
import com.paradiso.movie.search.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    FirebaseFirestore db;
    FirebaseAuth mAuth ;
    FirebaseUser firebaseUser;

    BottomNavigationView bottomNavigationView;
    Fragment selectedFragment;
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Window window = this.getWindow();

        window.setStatusBarColor(this.getResources().getColor(R.color.colorGray));

        int darkFlag = getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        if(darkFlag == Configuration.UI_MODE_NIGHT_YES) {
            window.setStatusBarColor(this.getResources().getColor(R.color.colorGray));


        }

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser =mAuth.getCurrentUser();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setSelected(true);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.home:
                        selectedFragment = new HomeFragment();
                        break;
                    case R.id.search:
                        selectedFragment = new SearchFragment();
                        break;
                    case R.id.profile:
                        SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                        editor.putString("profile_id",mAuth.getCurrentUser().getUid());
                        editor.apply();
                        selectedFragment = new ProfileFragment();
                        overridePendingTransition(0,0);
                        break;
                    case R.id.follower:
                        selectedFragment = new PreFollowerSearchFragment();
                        overridePendingTransition(0, 0);
                        break;
                    default:
                        break;
                }
                if (selectedFragment != null){
                   getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                   ,selectedFragment).commit();

                }
                return true;
            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                ,new HomeFragment()).commit();
    }


    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            switch (bottomNavigationView.getSelectedItemId()){
                case R.id.home:
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                break;
                case R.id.search:
                case R.id.follower:
                case R.id.profile:
                    selectedFragment = new HomeFragment();
                    bottomNavigationView.setSelectedItemId(R.id.home);
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                            ,selectedFragment).commit();
                    break;
                default:
                    break;
            }
//            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();

        }

    }
}