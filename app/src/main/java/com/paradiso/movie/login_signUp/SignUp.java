package com.paradiso.movie.login_signUp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.paradiso.movie.R;
import com.paradiso.movie.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SignUp extends AppCompatActivity {
    EditText email, password, name,secondPassword;
    Button signUp_btn;
    TextView logIn;
    User user;
    final String TAG = "Sing_Up";
    private FirebaseAuth mAuth;
    SharedPreferences sharedPreferences;

    FirebaseFirestore db;
    DatabaseReference databaseReference;
    VideoView videoView;
    ConstraintLayout parentCons;


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.colorGray));
//        sharedPreferences = getApplicationContext().getSharedPreferences("myShare", MODE_PRIVATE);
        parentCons = findViewById(R.id.constraintLayout);

        FirebaseApp.initializeApp(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        db = FirebaseFirestore.getInstance();
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        logIn = findViewById(R.id.log_in_textView);
        name = findViewById(R.id.userName);
        signUp_btn = findViewById(R.id.SignUp_btn1);

        videoView = findViewById(R.id.video_neon);
        Uri uri = Uri.parse("android.resource://"
                + getPackageName()
                + "/"
                + R.raw.neon3);
        videoView.setVideoURI(uri);
        videoView.start();

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);


            }
        });

        int darkFlag = getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        if(darkFlag == Configuration.UI_MODE_NIGHT_YES){
            signUp_btn.setForceDarkAllowed(false);
            signUp_btn.setBackgroundResource(R.drawable.button_border_red_dark_mode);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorGray));
        }
        signUp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerData();

            }
        });
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });
        parentCons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
            }
        });
    }

    public void registerData() {
        String mEmail = email.getText().toString();
        String mPassword = password.getText().toString();
        String mName = name.getText().toString();

        if(TextUtils.isEmpty(mEmail) || TextUtils.isEmpty(mPassword) || TextUtils.isEmpty(mName)){
            Toast.makeText(SignUp.this,"All fields are required",Toast.LENGTH_SHORT).show();
        }else if(mPassword.length()<6){
            Toast.makeText(SignUp.this,"password should be at least 6 character",Toast.LENGTH_SHORT).show();
        }else{
            mAuth.createUserWithEmailAndPassword(mEmail,mPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                       uploadUserData();
                            } else {
                                Toast.makeText(SignUp.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    public void uploadUserData() {

        String mName = name.getText().toString();
        String imageUrl = "https://firebasestorage.googleapis.com/v0/b/movie-257a9.appspot.com/o/avatar.png?alt=media&token=bb5fe9f4-e63e-47aa-a1e7-1490c149cc9c";
        String mUser_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        user = new User(mName,mUser_id,imageUrl,"");


        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        String userId = firebaseUser.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        databaseReference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "you are registered", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignUp.this, GettingUserId.class));
                } else {
                    Toast.makeText(SignUp.this, "some problems occurred.", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    private void hideKeyboard(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);
    }



}


