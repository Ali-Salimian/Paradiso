package com.paradiso.movie.profile;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

public class EditItemOfProfile extends AppCompatActivity {
    private static final String TAG ="EditItemOfProfile" ;
    ImageView backArrowEdit;
    TextView save,titre,titre2;
    EditText editText;
    FirebaseUser firebaseUser;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item_of_profile);
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.colorGray));
        int darkFlag = getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        if(darkFlag == Configuration.UI_MODE_NIGHT_YES) {
            window.setStatusBarColor(this.getResources().getColor(R.color.colorGray));


        }
        mAuth = FirebaseAuth.getInstance();
        firebaseUser= mAuth.getCurrentUser();
        backArrowEdit = findViewById(R.id.arrow_back_image);
        save = findViewById(R.id.save_for_edit);
        titre = findViewById(R.id.titre_text_view);
        titre2 = findViewById(R.id.titre2_text_view);
        editText = findViewById(R.id.contain_edit_text);

        Intent intent =getIntent();
        String contain = intent.getStringExtra("contain");
        String titreString = intent.getStringExtra("titre");
        String unique = intent.getStringExtra("unique");

        titre.setText(titreString);
        titre2.setText(titreString);
        editText.setText(contain);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string = editText.getText().toString();
                if(unique.equals("false")){

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid()).child(titreString);
                    databaseReference.setValue(string);
                    startActivity(new Intent(getApplicationContext(),EditProfileActivity.class));
                }else{
                    Query query = FirebaseDatabase.getInstance().getReference("users").orderByChild("userName").equalTo(string);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(!snapshot.exists() || string.equals(contain)){
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(Objects.requireNonNull(firebaseUser.getUid())).child("userName");
                                editText.setText(string);
                                databaseReference.setValue(string);
                                startActivity(new Intent(getApplicationContext(),EditProfileActivity.class));
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

        backArrowEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



    }
}