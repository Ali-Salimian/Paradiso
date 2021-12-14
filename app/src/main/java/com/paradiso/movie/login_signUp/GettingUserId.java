package com.paradiso.movie.login_signUp;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.paradiso.movie.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class GettingUserId extends AppCompatActivity {
    EditText userName;
    Button nextBtn;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getting_user_id);
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.colorGray));
        userName = findViewById(R.id.user_id_edit_text);
        nextBtn = findViewById(R.id.next_btn);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        int darkFlag = getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        if(darkFlag == Configuration.UI_MODE_NIGHT_YES){
            userName.setForceDarkAllowed(false);
            userName.setBackgroundResource(R.drawable.button_border_black_dark_mode);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorGray));

        }

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mUserName = userName.getText().toString();
                if (TextUtils.isEmpty(mUserName)){
                    Toast.makeText(getApplicationContext(), "User name can not be empty", Toast.LENGTH_SHORT).show();
                }else{
                    Query query = FirebaseDatabase.getInstance().getReference("users").orderByChild("userName").equalTo(mUserName);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(!snapshot.exists()){
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(Objects.requireNonNull(firebaseUser.getUid())).child("userName");
                                databaseReference.setValue(mUserName);
                                startActivity(new Intent(getApplicationContext(),InstructionActivity.class));
                            }else{
                                Toast.makeText(getApplicationContext(), "This user Name was already taken ", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }
        });


    }
}