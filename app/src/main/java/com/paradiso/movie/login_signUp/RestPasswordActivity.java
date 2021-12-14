package com.paradiso.movie.login_signUp;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.paradiso.movie.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class RestPasswordActivity extends AppCompatActivity {
    private static final String TAG = "RestPasswordActivity";
    EditText email;
    Button sendBtn;
    ImageView backArrow;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_pasword);
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.colorGray));
        int darkFlag = getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        if(darkFlag == Configuration.UI_MODE_NIGHT_YES) {
            window.setStatusBarColor(this.getResources().getColor(R.color.colorGray));


        }

        mAuth= FirebaseAuth.getInstance();

        email = findViewById(R.id.entered_email);
        sendBtn= findViewById(R.id.send_btn);
        backArrow = findViewById(R.id.back_arrow_search);
        sendBtn.setText("send");
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!email.getText().toString().equals("")){
                    mAuth.sendPasswordResetEmail(email.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "The recovery email is sent to your email address", Toast.LENGTH_LONG).show();
                                    sendBtn.setText("Resend");
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), "Something went wrong,Please try again", Toast.LENGTH_LONG).show();
                                }

                            }
                        });
                }else{
                    Toast.makeText(getApplicationContext(),"Please enter your email address",Toast.LENGTH_SHORT).show();
                }
            }
        });
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}