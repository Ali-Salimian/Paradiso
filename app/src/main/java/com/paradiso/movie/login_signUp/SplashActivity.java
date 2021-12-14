package com.paradiso.movie.login_signUp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Window;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.paradiso.movie.MainActivity;
import com.paradiso.movie.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.colorGray));

        int darkFlag = getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        if(darkFlag == Configuration.UI_MODE_NIGHT_YES) {
            window.setStatusBarColor(this.getResources().getColor(R.color.colorGray));


        }


        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();


        if(isConnected(getApplicationContext())) {
            if(firebaseUser != null) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(Objects.requireNonNull(firebaseUser.getUid()));
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {


                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild("userName")){
                            String userName = Objects.requireNonNull(snapshot.child("userName").getValue()).toString();
                            if (userName.equals("")){
                                startActivity(new Intent(getApplicationContext(), GettingUserId.class));
                            }else{
                                Intent intent;
                                intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            }
                        }

                        else if ( !snapshot.hasChild("userName")) {
                            startActivity(new Intent(getApplicationContext(), GettingUserId.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }else{
                Intent intent;
                intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }

        }else{

            Intent intent = new Intent(getApplicationContext(), NoInternetActivity.class);
            startActivity(intent);

        }
    }

    private boolean isConnected(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return wifiConn != null && wifiConn.isConnected() || (mobileConn != null && mobileConn.isConnected());

    }
}