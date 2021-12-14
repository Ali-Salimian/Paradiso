package com.paradiso.movie.search;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.paradiso.movie.R;

public class PopActivity extends Activity {
    private static final String TAG = "POP";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_window);
        DisplayMetrics ds = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(ds);
        int withs = ds.widthPixels;
        int height = ds.heightPixels;
        getWindow().setLayout((int) (withs*.8),(int) (height*.5) );


        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.x=0;
        params.y = -getWindowManager().getDefaultDisplay().getHeight()/2;
        getWindow().setAttributes(params);

    }

}
