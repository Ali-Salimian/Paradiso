package com.paradiso.movie.search;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.paradiso.movie.R;
import com.paradiso.movie.adaptor.GenreAdapter;
import com.paradiso.movie.model.MovieData;
import com.paradiso.movie.model.User;
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
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

import io.reactivex.rxjava3.annotations.NonNull;

public class CreateGroup extends AppCompatActivity {
    private static final String TAG = "CreateGroup";
    EditText groupName;
    TextView createGroup;
    String id;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser firebaseUser;
    User currentUser;
    String editOrNot;
    ImageView backArrow;
    RecyclerView recyclerView;
    ImageView selectedMoviePoster;
    int rate;
    ArrayList<String> groupNameList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        Window window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.colorGray));
        int darkFlag = getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        if(darkFlag == Configuration.UI_MODE_NIGHT_YES) {
            window.setStatusBarColor(this.getResources().getColor(R.color.colorGray));


        }

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        rate = intent.getIntExtra("rate",0);
        editOrNot = intent.getStringExtra("editOrNot");

        Bundle bundle = intent.getExtras();
        MovieData movieData = (MovieData) bundle.getSerializable("movieData");
        groupName = findViewById(R.id.group_name_edit);

        createGroup = findViewById(R.id.create_group_TextView);
        backArrow = findViewById(R.id.backWard_group);
        recyclerView = findViewById(R.id.recyclerView_genre_create);

        selectedMoviePoster = findViewById(R.id.poster_movie_create);

        groupNameList = new ArrayList<>();
        getListOfGroups();

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        Glide.with(getApplicationContext()).load(movieData.getPosterUrl()).into(selectedMoviePoster);

        ArrayList<String> genres = new ArrayList<>();
        String genresOfMovie = movieData.getGenre();
        String[] separated = genresOfMovie.split(",");
        genres.addAll(Arrays.asList(separated));
        genres.add(movieData.getDirector());
        for(int i=0;i<4;i++){
            String actorName = movieData.getActors().get(i).getName();
            genres.add(actorName);
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(),RecyclerView.HORIZONTAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        GenreAdapter genreAdapter =new GenreAdapter(genres,getApplicationContext(),new GenreAdapter.ItemClickListener(){
            @Override
            public void onItemClick(String genre) {
                groupName.setText(genre);
            }
        });
        recyclerView.setAdapter(genreAdapter);

        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(groupName.getText()!=null){
                    if(groupNameList.contains(groupName.getText().toString())){
                        Log.i(TAG, "onClick: This Group Exists");
                    }
                    else{
                        movieData.setMyRate(String.valueOf(rate));
                        if (50 <= rate && rate <= 60) {
                            uploadMovie("51-60",groupName.getText().toString(),movieData);
                        } else if (61 <= rate && rate <= 70) {
                            uploadMovie("61-70",groupName.getText().toString(),movieData);
                        }
                        else if (71 <= rate && rate <= 80) {
                            uploadMovie("71-80",groupName.getText().toString(),movieData);
                        } else if (81 <= rate && rate <= 90) {
                            uploadMovie("81-90",groupName.getText().toString(),movieData);

                        } else if (91 <= rate && rate <= 100) {
                            uploadMovie("91-100",groupName.getText().toString(),movieData);

                        }
                    }

                }
            }
        });

    }

    public void uploadMovie(String titleRate,String titleGenre,MovieData movieData){

        if (editOrNot.equals("Add to Favourite")){
            movieData.setDate(System.currentTimeMillis());
            movieData.setSavedGroup(titleRate);
            movieData.setSavedTitleGroup(titleGenre);
            String postId = FirebaseDatabase.getInstance().getReference("posts").push().getKey();
            movieData.setPostId(postId);
            assert postId != null;
            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("posts").child(postId);
            databaseRef.setValue(movieData).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("movies").child(firebaseUser.getUid()).child("rate").child(titleRate).child(id);
                        firebaseDatabase.setValue(movieData).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    DatabaseReference firebaseDatabase1 = FirebaseDatabase.getInstance().getReference("movies").child(firebaseUser.getUid()).child("genre").child(titleGenre).child(id);
                                    firebaseDatabase1.setValue(movieData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(getApplicationContext(),"The movie is saved",Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        }
                                    });


                                }else{
                                    Toast.makeText(getApplicationContext(),"The movie is NOT saved",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                                Toast.makeText(getApplicationContext(),"The movie is NOT saved",Toast.LENGTH_SHORT).show();
                            }
                        });

                    }else{
                        Toast.makeText(getApplicationContext(),"The movie is NOT saved",Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }else{
            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("posts").child(movieData.getPostId());
            databaseRef.child("myRate").setValue(String.valueOf(rate));
            databaseRef.child("savedGroup").setValue(titleRate);
            databaseRef.child("savedTitleGroup").setValue(titleGenre);
            DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("movies").child(firebaseUser.getUid()).child("rate").child(movieData.getSavedGroup()).child(movieData.getId());
            firebaseDatabase.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Void> task) {
                    if(task.isSuccessful()){
                        DatabaseReference firebaseDatabase0 = FirebaseDatabase.getInstance().getReference("movies").child(firebaseUser.getUid()).child("genre").child(movieData.getSavedTitleGroup()).child(movieData.getId());
                        firebaseDatabase0.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                                uploadEditedMovie(titleRate,titleGenre,movieData);
                            }
                        });
                    }else{
                        Toast.makeText(getApplicationContext(),"The movie is NOT edited",Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@io.reactivex.rxjava3.annotations.NonNull @NotNull Exception e) {
                    Toast.makeText(getApplicationContext(),"The movie is NOT edited",Toast.LENGTH_SHORT).show();
                }
            });

        }





    }
    public void uploadEditedMovie(String titleRate,String titleGenre,MovieData movieData){
        movieData.setSavedGroup(titleRate);
        movieData.setSavedTitleGroup(titleGenre);
        DatabaseReference firebaseDatabase1 = FirebaseDatabase.getInstance().getReference("movies").child(firebaseUser.getUid()).child("rate").child(titleRate).child(id);
        firebaseDatabase1.setValue(movieData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if(task.isSuccessful()){
                    DatabaseReference firebaseDatabase2 = FirebaseDatabase.getInstance().getReference("movies").child(firebaseUser.getUid()).child("genre").child(titleGenre).child(id);
                    firebaseDatabase2.setValue(movieData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"The movie is Edited",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });

                }else{
                    Toast.makeText(getApplicationContext(),"The movie is NOT edited",Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(getApplicationContext(),"The movie is NOT edited",Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void getUserInfo(){
        DatabaseReference databaseReference  = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull @NotNull DataSnapshot snapshot) {
                if(getApplicationContext() == null){
                    return;
                }
                currentUser = snapshot.getValue(User.class);

            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull @NotNull DatabaseError error) {

            }
        });


    }
    public void getListOfGroups() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("movies").child(firebaseUser.getUid()).child("genre");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {

                for (DataSnapshot data : snapshot.getChildren()) {
                    String groupName = data.getKey();
                    groupNameList.add(groupName);
                }
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });
    }
}