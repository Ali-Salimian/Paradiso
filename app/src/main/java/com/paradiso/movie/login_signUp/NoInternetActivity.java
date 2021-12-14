package com.paradiso.movie.login_signUp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.paradiso.movie.R;

public class NoInternetActivity extends AppCompatActivity {
    Button tryAgainBtn,checkInternetBtn;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.colorGray));
        tryAgainBtn = findViewById(R.id.try_again_btn);
        checkInternetBtn = findViewById(R.id.check_internet_btn);

        int darkFlag = getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        if(darkFlag == Configuration.UI_MODE_NIGHT_YES) {
            window.setStatusBarColor(this.getResources().getColor(R.color.colorGray));


        }

        if (isConnected(getApplicationContext())){
            startActivity(new Intent(getApplicationContext(),SplashActivity.class));
        }

        tryAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),SplashActivity.class));
            }
        });

        checkInternetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        });
    }

    private boolean isConnected(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return wifiConn != null && wifiConn.isConnected() || (mobileConn != null && mobileConn.isConnected());

    }
}