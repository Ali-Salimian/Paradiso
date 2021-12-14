package com.paradiso.movie.login_signUp;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.paradiso.movie.MainActivity;
import com.paradiso.movie.R;

public class InstructionActivity extends AppCompatActivity {
    Button startBtn;
    TextView textView1,textView2,textView3,textView4;
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.colorGray));
        startBtn = findViewById(R.id.start_btn);
        textView1=findViewById(R.id.textView78);
        textView2=findViewById(R.id.textView7);
        textView3=findViewById(R.id.textView8);
        textView4=findViewById(R.id.text_view_4);
        int darkFlag = getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        if(darkFlag == Configuration.UI_MODE_NIGHT_YES){
            textView1.setForceDarkAllowed(false);
            textView1.setBackgroundResource(R.drawable.custom_rounded_corner_white_dark_mode);
            textView2.setForceDarkAllowed(false);
            textView2.setBackgroundResource(R.drawable.custom_rounded_corner_white_dark_mode);
            textView3.setForceDarkAllowed(false);
            textView3.setBackgroundResource(R.drawable.custom_rounded_corner_white_dark_mode);
            textView4.setForceDarkAllowed(false);
            textView4.setBackgroundResource(R.drawable.custom_rounded_corner_white_dark_mode);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorGray));

        }
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }
}