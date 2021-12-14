package com.paradiso.movie.search;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.paradiso.movie.R;
import com.paradiso.movie.adaptor.ActorAdapter;
import com.paradiso.movie.adaptor.GenreAdapter;
import com.paradiso.movie.adaptor.ImageAdapter;
import com.paradiso.movie.adaptor.PopListAdapter;
import com.paradiso.movie.model.ActorModel;
import com.paradiso.movie.model.MovieData;
import com.paradiso.movie.model.PopItemList;
import com.paradiso.movie.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import io.reactivex.rxjava3.annotations.NonNull;


public class SelectedMovieFragment extends Fragment {
    private static final String TAG = "SelectedMovieActivity";
    ArrayList<String> genres = new ArrayList<>();
    VideoView trailerVV;
    TextView titleTV,descriptionTV;
    ImageView posterIV ;
    RecyclerView recyclerView;
    MovieData movieData;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    Context mContext;
    Button addToFav;
    FirebaseUser firebaseUser;
    String id;
    AlertDialog.Builder dialogBuilder;
    AlertDialog dialog;
    String selectedGroup ="";
    int i1 = 0;
    ImageView cancel_btn;
    View view;

    User currentUser;
    int rate;
    MovieData existedMovieData;
    ConstraintLayout constraintLayout;
    PopupWindow popupWindow;
    ScrollView scrollView;


    TextView pop_text_view,seekBarResult;
    SeekBar pop_inserted_rate_seekBar;
    ConstraintLayout constraintLayout1;
    RelativeLayout rel_layout ;
    BottomNavigationView bottomNavigationView,bottomNavigationView1;
    RecyclerView recyclerImages;
    RecyclerView recyclerActorList;
    TextView imdb_rate,meta_rate,rotten_rate;
    Button addTOWatchlist;
    Button findBigFans;
    ImageView backArrow;
    private int mSampleDurationTime = 1000; // 1 sec
    private boolean continueToRun = true;
    ImageView loadingPoster;
    ProgressBar progressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=  inflater.inflate(R.layout.activity_selected_movie_activitiy, container, false);
        assert getArguments() != null;
        id = getArguments().getString("id");
        String posterUrl = getArguments().getString("posterUrl");
        loadingPoster = view.findViewById(R.id.loading_poster);

        Glide.with(view.getContext()).load(posterUrl).into(loadingPoster);

        scrollView = view.findViewById(R.id.scrollView);
        scrollView.setVisibility(View.GONE);
        loadingPoster.setVisibility(View.VISIBLE);
        findBigFans = view.findViewById(R.id.find_big_fans);
        recyclerView = view.findViewById(R.id.horizontal_genre_rec);

        addToFav = view.findViewById(R.id.favourite_btn);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        titleTV = view.findViewById(R.id.title_textView);
        descriptionTV = view.findViewById(R.id.description_textView);
        posterIV = view.findViewById(R.id.poster_imageView);
        recyclerImages = view.findViewById(R.id.image_recyclerView);
        recyclerActorList = view.findViewById(R.id.actor_recycler);
        addTOWatchlist = view.findViewById(R.id.watchList_btn_selected);

        constraintLayout = view.findViewById(R.id.constraintLayout2);

        rel_layout = view.findViewById(R.id.rel_layout);

        imdb_rate = view.findViewById(R.id.imdb_rate);
        meta_rate = view.findViewById(R.id.meta_rate);
        rotten_rate = view.findViewById(R.id.rot_rate);
        backArrow = view.findViewById(R.id.back_arrow_selected);

        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);


        findBigFans.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                PeopleWhoInsertTopRated peopleFragment = new PeopleWhoInsertTopRated();
                Bundle bundle1 = new Bundle();
                bundle1.putString("id",id);
                peopleFragment.setArguments(bundle1);

                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container,peopleFragment).addToBackStack("SelectedMovieFragment").commit();


            }
        });

        MovieTask movieTask = new MovieTask();

        movieTask.execute("https://imdb-api.com/en/API/Title/k_lfd0fp87/"+id+"/Posters,Images,Trailer,");

        addToFav.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                onButtonShowPopupWindowClick();
            }
        });



        addTOWatchlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btn = addTOWatchlist.getText().toString();
                if(btn.equals("+Watchlist")){
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("watchlist").child(firebaseUser.getUid()).child(movieData.getId());
                    databaseReference.setValue(movieData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@androidx.annotation.NonNull Task<Void> task) {

                            Toast.makeText(getContext(), movieData.getName()+"added to watchlist",Toast.LENGTH_SHORT).show();
                        }
                    });
                }else if(btn.equals("Added to watchlist")){
                    Toast.makeText(getContext(), movieData.getName()+"has already been added to watchList",Toast.LENGTH_SHORT).show();

                }
            }
        });
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();

            }
        });

        return view;

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onButtonShowPopupWindowClick() {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = layoutInflater.inflate(R.layout.popup_window,null);
        cancel_btn=customView.findViewById(R.id.cancel_image_pop);
        seekBarResult = customView.findViewById(R.id.seekBarResult_pop);
//        save_btn = customView.findViewById(R.id.save_btn_pop);
        pop_text_view = customView.findViewById(R.id.pop_text_view);
        pop_inserted_rate_seekBar = customView.findViewById(R.id.pop_inserted_rate_seekBar);
        ConstraintLayout parenConsPop = customView.findViewById(R.id.constraintLayout3_pop);
        ListView listView = customView.findViewById(R.id.pop_list_view);

        bottomNavigationView = customView.findViewById(R.id.bottom_create_group);

        WindowManager wm = (WindowManager) getView().getContext().getSystemService(Context.WINDOW_SERVICE);
        popupWindow = new PopupWindow(customView, LinearLayout.LayoutParams.MATCH_PARENT,wm.getDefaultDisplay().getHeight()*3/4,true);

        int darkFlag = getContext().getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        if(darkFlag == Configuration.UI_MODE_NIGHT_YES){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                parenConsPop.setForceDarkAllowed(false);
            }
            parenConsPop.setBackgroundResource(R.drawable.custom_dark_background_dark_mode);

        }

        popupWindow.showAtLocation(rel_layout,Gravity.BOTTOM,0,-1000);

        getMovieData(listView);
        seekBarResult.setText(""+50);
        pop_inserted_rate_seekBar.setMin(50);
        pop_inserted_rate_seekBar.setMax(100);
        pop_inserted_rate_seekBar.setProgress(50);
        if (addToFav.getText().equals("Edit")){
            pop_inserted_rate_seekBar.setProgress(Integer.parseInt(existedMovieData.getMyRate()));
            seekBarResult.setText(existedMovieData.getMyRate());
        }
        pop_inserted_rate_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarResult.setText(""+progress);
                rate = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@androidx.annotation.NonNull MenuItem item) {
                if(pop_inserted_rate_seekBar.getProgress()<50){
                    Toast.makeText(getContext(), "Please insert a rate ", Toast.LENGTH_SHORT).show();
                }else{

                    Intent intent = new Intent(getContext(),CreateGroup.class);
                    intent.putExtra("id",id);
                    intent.putExtra("rate",rate);
                    if(addToFav.getText() == "Edit"){
                        intent.putExtra("movieData",existedMovieData);

                    }else{
                        intent.putExtra("movieData",movieData);
                    }
                    intent.putExtra("editOrNot",addToFav.getText());
                    popupWindow.dismiss();
                    startActivity(intent);
                }

                return true;

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PopItemList popItemList = (PopItemList) listView.getItemAtPosition(position);
                String title = popItemList.getTitle();
                int rate = pop_inserted_rate_seekBar.getProgress();
                if(rate != -1){
                    String selectedRateGroup = "" ;
                    if (50 <= rate && rate <= 100) {
                        movieData.setMyRate(String.valueOf(rate));
                        Log.i(TAG, "onClick: " +rate);
                        if (50 <= rate && rate <= 60) {
                            selectedRateGroup = "51-60";
                        } else if (61 <= rate && rate <= 70) {
                            selectedRateGroup = "61-70";
                        }
                        else if (71 <= rate && rate <= 80) {
                            selectedRateGroup = "71-80";
                        } else if (81 <= rate && rate <= 90) {
                            selectedRateGroup = "81-90";
                        } else if (91 <= rate && rate <= 100) {
                            selectedRateGroup = "91-100";
                        }
                        uploadMovie(selectedRateGroup,title);
                        popupWindow.dismiss();
                    } else {
                        Toast.makeText(getContext(), "the rate must be between 50 to 100", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getContext(),"please insert a rate",Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    public void uploadMovie(String selectedRateGroup,String titleOfGroup){
        movieData.setSavedGroup(selectedRateGroup);
        movieData.setSavedTitleGroup(titleOfGroup);
        movieData.setDate(System.currentTimeMillis());
//        Log.i(TAG, "uploadMovie: "+movieData.getPostId());
//        Log.i(TAG, "uploadMovie: "+movieData.getId());
        if (addToFav.getText() == "Add to Favourite"){


//        movieData.setGenre(titleOfGroup);

            String postId = FirebaseDatabase.getInstance().getReference("posts").push().getKey();
            movieData.setPostId(postId);
            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("posts").child(postId);
            databaseRef.setValue(movieData).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("movies").child(firebaseUser.getUid()).child("rate").child(selectedRateGroup).child(id);
                        firebaseDatabase.setValue(movieData).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    DatabaseReference firebaseDatabase1 = FirebaseDatabase.getInstance().getReference("movies").child(firebaseUser.getUid()).child("genre").child(titleOfGroup).child(id);
                                    firebaseDatabase1.setValue(movieData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(getContext(),"The movie is saved",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });


                                }else{
                                    Toast.makeText(getContext(),"The movie is NOT saved",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                                Toast.makeText(getContext(),"The movie is NOT saved",Toast.LENGTH_SHORT).show();
                            }
                        });

                    }else{
                        Toast.makeText(getContext(),"The movie is NOT saved",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("posts").child(existedMovieData.getPostId());
            databaseRef.child("myRate").setValue(String.valueOf(rate));
            databaseRef.child("savedGroup").setValue(selectedRateGroup);
            databaseRef.child("savedTitleGroup").setValue(titleOfGroup);



            DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("movies").child(firebaseUser.getUid()).child("rate").child(existedMovieData.getSavedGroup()).child(existedMovieData.getId());
            firebaseDatabase.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@io.reactivex.rxjava3.annotations.NonNull @NotNull Task<Void> task) {
                    if(task.isSuccessful()){
                        DatabaseReference firebaseDatabase1 = FirebaseDatabase.getInstance().getReference("movies").child(firebaseUser.getUid()).child("genre").child(existedMovieData.getSavedTitleGroup()).child(existedMovieData.getId());
                        firebaseDatabase1.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                            }
                        });
                    }else{
                        Toast.makeText(getContext(),"The movie is NOT edited",Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@io.reactivex.rxjava3.annotations.NonNull @NotNull Exception e) {
                    Toast.makeText(getContext(),"The movie is NOT edited",Toast.LENGTH_SHORT).show();
                }
            });

            DatabaseReference firebaseDatabase1 = FirebaseDatabase.getInstance().getReference("movies").child(firebaseUser.getUid()).child("rate").child(selectedRateGroup).child(id);
            firebaseDatabase1.setValue(movieData).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Void> task) {
                    if(task.isSuccessful()){
                        DatabaseReference firebaseDatabase1 = FirebaseDatabase.getInstance().getReference("movies").child(firebaseUser.getUid()).child("genre").child(titleOfGroup).child(id);
                        firebaseDatabase1.setValue(movieData).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getContext(),"The movie is Edited",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                    }else{
                        Toast.makeText(getContext(),"The movie is NOT edited",Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    Toast.makeText(getContext(),"The movie is NOT edited",Toast.LENGTH_SHORT).show();
                }
            });



        }




    }


    private void initGenreRecyclerVIew(ArrayList<String> genres){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(), RecyclerView.HORIZONTAL,false);
        RecyclerView recyclerView = view.findViewById(R.id.horizontal_genre_rec);
        recyclerView.setLayoutManager(linearLayoutManager);
        GenreAdapter genreAdapter = new GenreAdapter(genres,getContext());
        recyclerView.setAdapter(genreAdapter);


    }

    private void SetMovieData(MovieData movieData){

        checkUploadedMovies(movieData);
        titleTV.setText(movieData.getName());
        descriptionTV.setText(movieData.getDescription());
        String genresOfMovie = movieData.getGenre();
        String[] separated = genresOfMovie.split(",");
        genres.addAll(Arrays.asList(separated));
        ImageAdapter imageAdapter = new ImageAdapter(movieData.getImageList(),getContext());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false);
        recyclerImages.setLayoutManager(linearLayoutManager);
        recyclerImages.setAdapter(imageAdapter);
        initGenreRecyclerVIew(genres);

        if(movieData.getActors()!=null){
            LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false);
            ActorAdapter actorAdapter = new ActorAdapter(movieData.getActors(),getContext());
            recyclerActorList.setLayoutManager(linearLayoutManager1);
            recyclerActorList.setAdapter(actorAdapter);
        }
        imdb_rate.setText(movieData.getImdbRate());
        meta_rate.setText(movieData.getMetaRate());
        rotten_rate.setText("");


        Glide.with(getContext()).asBitmap().load(movieData.getPosterUrl()).into(posterIV);

//        Uri uri = Uri.parse(movieData.getTeaserUrl());
//        trailerVV.setVideoURI(uri);
//        trailerVV.requestFocus();
//        trailerVV.start();

    }

    public class MovieTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {

            String result="";
            URL url;
            HttpURLConnection urlCon = null;
//            OkHttpClient client = new OkHttpClient();
//
//            Request request = new Request.Builder()
//                    .url(urls[0])
//                    .get()
//
//                    .build();
//
//            try {
//                Response response = client.newCall(request).execute();
//                return response.body().string();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            try {
                url = new URL(urls[0]);
                urlCon = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlCon.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                int data = inputStreamReader.read();
                while (data != -1){
                    char current = (char) data;
                    result += current;
                    data = inputStreamReader.read();

                }
                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return String.valueOf(e);
            } catch (IOException e) {
                e.printStackTrace();
                return String.valueOf(e);
            }


        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                if (result!=null) {
                    ArrayList<ActorModel> actorModels = new ArrayList<>();

                    JSONObject jsonObject = new JSONObject(result);

                    String title = jsonObject.getString("title");
                    String id = jsonObject.getString("id");
                    String posterUrl = jsonObject.getString("image");
                    String year = jsonObject.getString("year");
                    String director = jsonObject.getString("directors");
                    String description = jsonObject.getString("plot");
                    JSONObject jsonObjectTrailer = jsonObject.getJSONObject("trailer");
                    String teaserUrl = jsonObjectTrailer.getString("linkEmbed");
                    String genre = jsonObject.getString("genres");
                    String imdbRate = jsonObject.getString("imDbRating");
                    String metaRate = jsonObject.getString("metacriticRating");
                    JSONArray jsonArrayActor = jsonObject.getJSONArray("actorList");
                    int max1 = Math.min(jsonArrayActor.length(), 6);
                    for (int i = 0; i < max1; i++) {
                        JSONObject actorObject = jsonArrayActor.getJSONObject(i);
                        String actorName = actorObject.getString("name");
                        String actorId = actorObject.getString("id");
                        String actorUrlImage = actorObject.getString("image");
                        ActorModel actorModel = new ActorModel(actorName, actorId, actorUrlImage);
                        actorModels.add(actorModel);

                    }
                    JSONObject jsonObjectImages = jsonObject.getJSONObject("images");
                    JSONArray jsonArrayItems = jsonObjectImages.getJSONArray("items");
                    ArrayList<String> imageList = new ArrayList<>();
                    int max = Math.min(jsonArrayItems.length(), 6);
                    for (int i = 0; i < max; i++) {
                        JSONObject imageObject = jsonArrayItems.getJSONObject(i);
                        String imageUrl = imageObject.getString("image");

                        imageList.add(imageUrl);

                    }


                    getUserInfo();


                    movieData = new MovieData(id, title, director, genre, year, posterUrl, teaserUrl, imdbRate, metaRate, description, firebaseUser.getUid(), actorModels);
                    movieData.setImageList(imageList);
                    SetMovieData(movieData);
                    checkWatchlist(movieData);

                    loadingPoster.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);
                    return;
                }

            } catch (JSONException e) {

                e.printStackTrace();
            }

        }
    }

    public void getMovieData(ListView listView) {
        ArrayList<PopItemList> groupModel = new ArrayList<>();
        i1 = 0;
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("movies").child(firebaseUser.getUid()).child("genre");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                int numberOfGroups = (int) (long) snapshot.getChildrenCount();
                for (DataSnapshot data : snapshot.getChildren()) {
                    String groupName = data.getKey();
                    assert groupName != null;
                    databaseRef.child(groupName).limitToFirst(1).addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot1) {
                            int numberOfMovies = (int) snapshot1.getChildrenCount();


                            for (DataSnapshot data1 : snapshot1.getChildren()) {
                                i1++;
                                MovieData movieData = data1.getValue(MovieData.class);
                                PopItemList popItemList = new PopItemList(groupName,movieData.getPosterUrl());
                                groupModel.add(popItemList);


                            }
                            if (i1 == numberOfGroups ) {

                                ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) listView.getLayoutParams();
                                lp.height =numberOfGroups*296;
                                listView.setLayoutParams(lp);
                                listView.requestLayout();
                                ConstraintLayout.LayoutParams lp1 = (ConstraintLayout.LayoutParams) listView.getLayoutParams();

                                PopListAdapter adapter = new PopListAdapter(getContext(),R.layout.cart_popup_list,groupModel);
                                listView.setAdapter(adapter);


                            }

                        }

                        @Override
                        public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

                        }
                    });


                }
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });
    }
    private void getUserInfo(){
        DatabaseReference databaseReference  = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull @NotNull DataSnapshot snapshot) {
                if(getContext() == null){
                    return;
                }
                currentUser = snapshot.getValue(User.class);

            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull @NotNull DatabaseError error) {

            }
        });


    }

    private void checkWatchlist(MovieData movieData){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("watchlist")
                .child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.child(movieData.getId()).exists()){
                    addTOWatchlist.setText("Added to watchlist");

                }else {
                    addTOWatchlist.setText("+Watchlist");
                }
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void checkUploadedMovies(MovieData movieData){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("movies")
                .child(firebaseUser.getUid()).child("rate");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {

                if (!snapshot.exists()){
                    addToFav.setText("Add to Favourite");
                }
                addToFav.setText("Add to Favourite");
                for(DataSnapshot data: snapshot.getChildren()){

                    for(DataSnapshot dat:data.getChildren()){
                        if(Objects.equals(dat.getKey(), movieData.getId())){
                            existedMovieData = dat.getValue(MovieData.class);
                            addToFav.setText("Edit");
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull @NotNull DatabaseError error) {

            }
        });

    }

}