package com.paradiso.movie.profile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.paradiso.movie.R;
import com.paradiso.movie.adaptor.HorizonAdapter;
import com.paradiso.movie.adaptor.ProfileAdapter;
import com.paradiso.movie.login_signUp.Login;
import com.paradiso.movie.model.MovieData;
import com.paradiso.movie.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
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


public class ProfileFragment extends Fragment {
    static final int profileNumber = 0;
    private static final String TAG = "Profile";
    private static boolean run = true;
    BottomNavigationView bottomNavigationView;
    FirebaseUser firebaseUser ;
    String profileId;
    ConstraintLayout constraintLayout;
    ProfileAdapter profileAdapter;



    TextView groupsByRate,groupsByGenre;
    ImageView imageProfile,options;
    TextView followers,following,fullName,bio,userName,percentOfMatch;
    Button edit_profile;

    ArrayList<String> userFavMov= new ArrayList<>();
    ArrayList<String> otherUserFav= new ArrayList<>();
    ArrayList<String> matchedMoviesList= new ArrayList<>();

//    ArrayList<MovieData> movies = new ArrayList<>();
    int numberOfMatchedMovies = 0;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    LinearLayout mainLin;
    TextView groupName;
    RecyclerView recyclerView;
    LinearLayout subLin;
    int i1=0;
    int i2=0;
    User profileUser;
    User currentUser;
    ProgressBar seekBarPercentOfMatch;
    TextView filterRate;
    ImageView noContentImage;

    ConstraintLayout noContent;
    RecyclerView recyclerViewGenre;
    RecyclerView recyclerViewRate;
    RecyclerView watchRecycler;
    ImageView backArrow;
    TextView followersText,followingText,watchlistText;
    ArrayList<MovieData> watchlist = new ArrayList<>();



//    TimerTask timerTask = new TimerTask(){
//        @Override
//        public void run() {
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    changeText();
//                }
//            });
//        }
//    };

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_profile, container, false);
        filterRate = view.findViewById(R.id.filter_rate);
        filterRate.setVisibility(View.GONE);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileId=prefs.getString("profile_id","none");
        watchRecycler =view.findViewById(R.id.watchlist_recycler_view_profile);
        imageProfile = view.findViewById(R.id.image_profile);
        followers = view.findViewById(R.id.followers);
        following = view.findViewById(R.id.following);
        followingText = view.findViewById(R.id.following_text);
        followersText = view.findViewById(R.id.followers_text);
        fullName = view.findViewById(R.id.full_name);
        bio = view.findViewById(R.id.bio);
        userName=view.findViewById(R.id.user_name_profile);
        options = view.findViewById(R.id.options);
        edit_profile = view.findViewById(R.id.edit_profile);
        percentOfMatch = view.findViewById(R.id.text_percent_of_match);
        seekBarPercentOfMatch = view.findViewById(R.id.seekBar_percent_of_match);
        mainLin = view.findViewById(R.id.main_linear);
        constraintLayout = view.findViewById(R.id.cons);
        groupsByGenre = view.findViewById(R.id.titre_group_genre);
        groupsByRate = view.findViewById(R.id.titre_group_rate);
        groupsByRate.setVisibility(View.GONE);
        groupsByGenre.setVisibility(View.GONE);
        recyclerViewGenre = view.findViewById(R.id.main_recycler_profile_genre);
        recyclerViewRate = view.findViewById(R.id.main_recycler_profile);
        noContent = view.findViewById(R.id.no_content_profile);
        noContent.setVisibility(View.GONE);
        backArrow = view.findViewById(R.id.back_arrow_profile);
        percentOfMatch.setVisibility(View.GONE);
        noContentImage = view.findViewById(R.id.imageView6);

        watchlistText = view.findViewById(R.id.watchlist_text_profile);
        watchlistText.setVisibility(View.GONE);
        watchRecycler.setVisibility(View.GONE);
////

        //////
        int darkFlag = getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        if(darkFlag == Configuration.UI_MODE_NIGHT_YES){

            groupsByGenre.setBackgroundResource(R.color.colorBlack_darkMode);

            groupsByRate.setBackgroundResource(R.color.colorBlack_darkMode);
            recyclerViewGenre.setForceDarkAllowed(false);
            recyclerViewGenre.setBackgroundResource(R.color.colorBlack_darkMode);
            recyclerViewRate.setForceDarkAllowed(false);
            recyclerViewRate.setBackgroundResource(R.color.colorBlack_darkMode);
            userName.setForceDarkAllowed(false);
            userName.setTextColor(R.color.colorWhite_darkMode);
            options.setForceDarkAllowed(false);
            options.setImageResource(R.drawable.ic_optionn_black);
            edit_profile.setForceDarkAllowed(false);
            edit_profile.setBackgroundResource(R.drawable.button_border_red_dark_mode);
            userName.setForceDarkAllowed(false);
            userName.setTextColor(Color.WHITE);
            watchlistText.setForceDarkAllowed(false);
            watchlistText.setBackgroundResource(R.color.colorBlack_darkMode);
            watchRecycler.setForceDarkAllowed(false);
            watchRecycler.setBackgroundResource(R.drawable.custom_dark_mode);
            noContentImage.setForceDarkAllowed(false);
            noContentImage.setImageResource(R.drawable.empty_database1_dark_mode);
        }
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FollowersListFragment()).addToBackStack("ProfileFragment").commit();
            }
        });
        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FollowingsListFragment()).addToBackStack("ProfileFragment").commit();
            }
        });
        followersText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FollowersListFragment()).addToBackStack("ProfileFragment").commit();
            }
        });
        followingText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FollowingsListFragment()).addToBackStack("ProfileFragment").commit();
            }
        });

        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup();
            }
        });
        getUserInfo();
        getFollowers();

        if (profileId.equals(firebaseUser.getUid())){
            edit_profile.setText("Edit Profile");
            percentOfMatch.setVisibility(View.GONE);
            seekBarPercentOfMatch.setVisibility(View.GONE);
            filterRate.setVisibility(View.GONE);
            backArrow.setVisibility(View.GONE);
            getWatchlist();
        }else{
            calculatePercentOfMatch();
            checkFollow();
            percentOfMatch.setVisibility(View.VISIBLE);
            seekBarPercentOfMatch.setVisibility(View.VISIBLE);
            backArrow.setVisibility(View.VISIBLE);
//            timerTask.run();
//            if(run) {
//
//                Timer mTimer = new Timer();
//                mTimer.scheduleAtFixedRate(timerTask, 0, 3000);
//                run = false;
//
//            }else{
//                Log.i(TAG, "onCreateView: ddddd");
//            }



        }

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getContext().getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profile_id",firebaseUser.getUid());
                editor.apply();
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });

        if(!edit_profile.getText().toString().equals("Edit Profile")) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(profileId);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    profileUser = snapshot.getValue(User.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid());
            databaseReference1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    currentUser = snapshot.getValue(User.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }




        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btn = edit_profile.getText().toString();
                if(btn.equals("Edit Profile")){
                    startActivity(new Intent(getContext(),EditProfileActivity.class));
                }else if(btn.equals("follow")){

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileId).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId)
                            .child("followers").child(firebaseUser.getUid()).setValue(true);
                }else if(btn.equals("following")){

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileId).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId)
                            .child("followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });
        getMovieDataRate(view,"rate");
        getMovieDataGenre(view,"genre");

        return view;
    }

    private void getUserInfo(){
        DatabaseReference databaseReference  = FirebaseDatabase.getInstance().getReference("users").child(profileId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(getContext() == null){
                    return;
                }
                User user = snapshot.getValue(User.class);

                assert user != null;
                Glide.with(getContext()).load(user.getImageUrl()).into(imageProfile);
                userName.setText(user.getUserName());
                fullName.setText(user.getName());
                bio.setText("");
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


    }

    private void checkFollow(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.child(profileId).exists()){
                    edit_profile.setText("following");
                }else {
                    edit_profile.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void getFollowers(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profileId).child("followers");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                followers.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profileId).child("following");
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                following.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void calculatePercentOfMatch(){
        userFavMov.clear();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("movies")
                .child(firebaseUser.getUid()).child("rate");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                for (DataSnapshot data:snapshot.getChildren()){
                    if(data.getKey().equals("91-100") ||data.getKey().equals("81-90")||data.getKey().equals("70-80")){
                        for(DataSnapshot dat:data.getChildren()){
                            String nameOfMovie = dat.getKey();
                            userFavMov.add(nameOfMovie);
                        }
                    }
                }
                compareData(userFavMov);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }



    private void compareData(ArrayList<String> list) {
        otherUserFav.clear();
        numberOfMatchedMovies =0;
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("movies")
                .child(profileId).child("rate");
        databaseReference1.addValueEventListener(new ValueEventListener() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    if(data.getKey().equals("91-100") ||data.getKey().equals("81-90")||data.getKey().equals("71-80")){
                        for(DataSnapshot dat:data.getChildren()){
                            String nameOfMovie = dat.getKey();
                            otherUserFav.add(nameOfMovie);
                        }
                    }

                }
                for (int i =0;i<otherUserFav.size();i++){
                    if(list.contains(otherUserFav.get(i))){

                        numberOfMatchedMovies++;
                        matchedMoviesList.add(otherUserFav.get(i));

                    }
                }

                if(otherUserFav.size()!=0){
                    percentOfMatch.setText((numberOfMatchedMovies*100/otherUserFav.size())+"%");
                    seekBarPercentOfMatch.setProgress(numberOfMatchedMovies*100/otherUserFav.size());
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error){
            }
        });

    }

    public void getMovieDataRate(View view,String subGroup){

        ArrayList<String> nameOFGroups = new ArrayList<>();
        ArrayList<ArrayList<MovieData>> wholeMovies = new ArrayList<>();

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("movies").child(profileId).child(subGroup);
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists() && watchlist.size()==0){
                    noContent.setVisibility(View.VISIBLE);
                }
                int numberOfGroups=(int) (long)snapshot.getChildrenCount();
                i1=0;
                for (DataSnapshot data:snapshot.getChildren()){

                    String groupName = data.getKey();
                    nameOFGroups.add(groupName);
                    assert groupName != null;

                    databaseRef.child(groupName).addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot1) {
                            int numberOfMovies = (int) snapshot1.getChildrenCount();
                            i1++;
                            int i=0;
                            ArrayList<MovieData> movies = new ArrayList<>();
                            for (DataSnapshot data1:snapshot1.getChildren()){
                                i++;
                                MovieData movieData = data1.getValue(MovieData.class);
                                movies.add(movieData);
                                if(i==numberOfMovies){
                                    wholeMovies.add(movies);
                                }
                            }
                            if (i1 == numberOfGroups && i==numberOfMovies) {
//                                if(subGroup.equals("rate")){
                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

                                recyclerViewRate.setLayoutManager(linearLayoutManager);

                                recyclerViewRate.setNestedScrollingEnabled(false);
                                    profileAdapter = new ProfileAdapter(getContext(), nameOFGroups, wholeMovies,constraintLayout , profileId, subGroup);
                                recyclerViewRate.setAdapter(profileAdapter);
                                    groupsByRate.setVisibility(View.VISIBLE);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void getMovieDataGenre(View view,String subGroup){

        ArrayList<String> nameOFGroups = new ArrayList<>();
        ArrayList<ArrayList<MovieData>> wholeMovies = new ArrayList<>();

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("movies").child(profileId).child(subGroup);
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int numberOfGroups=(int) (long)snapshot.getChildrenCount();
                i2=0;
                for (DataSnapshot data:snapshot.getChildren()){
                    String groupName = data.getKey();
                    nameOFGroups.add(groupName);
                    assert groupName != null;
                    databaseRef.child(groupName).addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot1) {
                            int numberOfMovies = (int) snapshot1.getChildrenCount();
                            i2++;
                            int ii=0;
                            ArrayList<MovieData> movies = new ArrayList<>();
                            for (DataSnapshot data1:snapshot1.getChildren()){
                                ii++;
                                MovieData movieData = data1.getValue(MovieData.class);
                                movies.add(movieData);
                                if(ii==numberOfMovies){
                                    wholeMovies.add(movies);
                                }
                            }
                            if (i2 == numberOfGroups && ii==numberOfMovies) {
                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                                    recyclerViewGenre.setLayoutManager(linearLayoutManager);
                                    ProfileAdapter profileAdapter = new ProfileAdapter(getContext(), nameOFGroups, wholeMovies,constraintLayout,profileId,subGroup);
                                    recyclerViewGenre.setNestedScrollingEnabled(false);
                                    recyclerViewGenre.setAdapter(profileAdapter);
                                    groupsByGenre.setVisibility(View.VISIBLE);

                            }
                            else if(watchlist.size()!=0){
                                Log.i(TAG, "onDataChange: fffff");
                                watchlistText.setVisibility(View.VISIBLE);
                                watchRecycler.setVisibility(View.VISIBLE);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void showPopup(){
        View popupView = LayoutInflater.from(getContext()).inflate(R.layout.popup_window_profile_setting,null);

        ImageView cancel_btn=popupView.findViewById(R.id.cancel_image_pop_setting);
        TextView logout = popupView.findViewById(R.id.logout_profile_setting);
        int darkFlag = getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        if(darkFlag == Configuration.UI_MODE_NIGHT_YES){


            logout.setForceDarkAllowed(false);
            logout.setBackgroundResource(R.drawable.custom_dark_mode);
        }
        PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT,true);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
                prefs.edit().clear().apply();
                mAuth.signOut();
                Intent intent = new Intent(getContext(), Login.class);
                startActivity(intent);
                popupWindow.dismiss();
            }
        });
        popupWindow.showAtLocation(constraintLayout, Gravity.BOTTOM,0,0);
    }


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == 100){
            profileAdapter.deleteMoviesOfAGroup(item.getGroupId());
            Toast.makeText(getContext(), " all movies are deleted ", Toast.LENGTH_LONG).show();
        }
        return super.onContextItemSelected(item);
    }

    public void getWatchlist(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("watchlist").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot data:snapshot.getChildren()){
                    MovieData movieData = data.getValue(MovieData.class);
                    watchlist.add(movieData);
                }

                HorizonAdapter watchlistAdapter = new HorizonAdapter(watchlist,getContext(),firebaseUser.getUid(),0);
                LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
                watchRecycler.setLayoutManager(linearLayoutManager1);
                watchRecycler.setAdapter(watchlistAdapter);
                watchlistText.setVisibility(View.VISIBLE);
                watchRecycler.setVisibility(View.VISIBLE);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void changeText(){
        if(filterRate.getVisibility()==View.VISIBLE){
            filterRate.setVisibility(View.GONE);
            percentOfMatch.setVisibility(View.VISIBLE);
        }else{
            filterRate.setVisibility(View.VISIBLE);
            percentOfMatch.setVisibility(View.GONE);
        }


    }


}