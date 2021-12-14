package com.paradiso.movie.profile;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.paradiso.movie.R;
import com.paradiso.movie.model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {

    ImageView backArrowEdit,imageProfileEdit;
    TextView save,tv_change;
    TextView name,userName,bio;
    FirebaseUser firebaseUser;

    private Uri mImageUri;
    private StorageTask uploadTask;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.colorGray));
        int darkFlag = getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        if(darkFlag == Configuration.UI_MODE_NIGHT_YES) {
            window.setStatusBarColor(this.getResources().getColor(R.color.colorGray));


        }
        backArrowEdit = findViewById(R.id.arrow_back_image);
        imageProfileEdit = findViewById(R.id.image_profile_edit);
        save = findViewById(R.id.save_for_edit);
        tv_change = findViewById(R.id.tv_change);
        name = findViewById(R.id.full_name_edit);
        userName = findViewById(R.id.user_name_edit);
        bio = findViewById(R.id.bio_edit);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user =snapshot.getValue(User.class);
                assert user != null;
                name.setText(user.getName());
                userName.setText(user.getUserName());
//                bio.setText(user.getB);
                Glide.with(getApplicationContext()).load(user.getImageUrl()).into(imageProfileEdit);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        backArrowEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(EditProfileActivity.this);
            }
        });
        imageProfileEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(EditProfileActivity.this);
            }
        });
//        save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                updateProfile(name.getText().toString()
//                ,userName.getText().toString()
//                ,bio.getText().toString());
//            }
//        });
        userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getApplicationContext(),EditItemOfProfile.class);
                intent.putExtra("contain" ,userName.getText().toString());
                intent.putExtra("titre" ,"userName");
                intent.putExtra("unique","true");
                startActivity(intent);
            }
        });
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getApplicationContext(),EditItemOfProfile.class);
                intent.putExtra("contain" ,name.getText().toString());
                intent.putExtra("titre" ,"name");
                intent.putExtra("unique","false");
                startActivity(intent);
            }
        });
    }
    private void updateProfile(String fullName,String userName,String bio){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("fullName",fullName);
        hashMap.put("userName",userName);
        hashMap.put("bio",bio);
        ref.updateChildren(hashMap);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }



    private void uploadImage(){
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("uploading");
        progressDialog.show();

        if(mImageUri != null){
            StorageReference fileRef = storageReference.child(System.currentTimeMillis()
                    +"."+getFileExtension(mImageUri));
            uploadTask = fileRef.putFile(mImageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return  fileRef.getDownloadUrl();

                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String myUri = downloadUri.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("imageUrl",""+myUri);
                        reference.updateChildren(hashMap);
                        progressDialog.dismiss();

                    }else{
                        Toast.makeText(EditProfileActivity.this,"failed",Toast.LENGTH_SHORT).show();

                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditProfileActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();

                }
            });
        }else{
            Toast.makeText(this,"No image selected",Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            assert result != null;
            mImageUri = result.getUri();
            uploadImage();
        }else{
            Toast.makeText(this,"something went Wrong!",Toast.LENGTH_SHORT).show();

        }
    }
}