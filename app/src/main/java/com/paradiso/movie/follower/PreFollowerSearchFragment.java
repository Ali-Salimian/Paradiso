package com.paradiso.movie.follower;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paradiso.movie.R;
import com.paradiso.movie.adaptor.FollowAdapter;
import com.paradiso.movie.adaptor.SearchAdapter;
import com.paradiso.movie.model.MovieData;
import com.paradiso.movie.model.PreDataMovies;
import com.paradiso.movie.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Random;


public class PreFollowerSearchFragment extends Fragment {
    int followerNumber = 1;
    private static final String TAG = "SearchFragment";
    Context context;

    BottomNavigationView bottomNavigationView;
    MovieData movieData;
    private String id= "";
    FirebaseFirestore db ;
    FirebaseAuth mAuth;

    ConstraintLayout noContent;


    ArrayList<PreDataMovies> searchedList;
    SearchAdapter adapter ;
    ArrayList<MovieData> top10List = new ArrayList<>();


    FirebaseUser firebaseUser;

    private SearchAdapter.RecyclerViewClickListener listener;
//    GetID getID = new GetID();

    TextView searchBar;
    RecyclerView recyclerView,recyclerView2;



    ArrayList<MovieData> movies = new ArrayList<>();

    ImageView noContentImage;
    TextView nameOfFollower,nameOfFollower2 ;
    ImageView profileImageOfFollower,profileImageOfFollower2;
    ImageView posterOfSelectedMovie,posterOfSelectedMovie2;
    TextView selectedGroupName,selectedGroupName2;
    ConstraintLayout layout1,layout2;

    ArrayList<ArrayList<MovieData>> arrayListOfWholeMovies= new ArrayList<>();
    ArrayList<User> userList = new ArrayList<>();
    ArrayList<MovieData> selectedMovieList= new ArrayList<>();
    ArrayList<String> selectedGroupList= new ArrayList<>();

    int numberOfRound = 0;
    User currentUser;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pre_search_follower, container, false);
        recyclerView = view.findViewById(R.id.rec_pre_follower);
//        recyclerView2 = view.findViewById(R.id.searchedList2);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL,false);

        recyclerView.setLayoutManager(linearLayoutManager);
//        recyclerView2.setLayoutManager(linearLayoutManager1);
        noContent = view.findViewById(R.id.no_content_follower);
        noContent.setVisibility(View.GONE);
        searchBar = view.findViewById(R.id.search_bar);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        noContentImage = view.findViewById(R.id.imageView6);
        getCurrentUser();

        int darkFlag = getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        if(darkFlag == Configuration.UI_MODE_NIGHT_YES){
            noContentImage.setForceDarkAllowed(false);
            noContentImage.setImageResource(R.drawable.empty_database1_dark_mode);
        }




        searchedList = new ArrayList<>();
        getMovieData();

        searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        ,new FollowerFragment()).addToBackStack("A").commit();
            }
        });



        return view;
    }

    public void clearRecycler(){
        if(searchedList.size()>0) {
            searchedList.clear();
            adapter.notifyDataSetChanged();
        }
    }
    public void getMovieData(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("movies").child(firebaseUser.getUid()).child("rate");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap:snapshot.getChildren()){
                    for(DataSnapshot data:snap.getChildren()){
                        MovieData movieData = data.getValue(MovieData.class);
                        assert movieData != null;
                        top10List.add(movieData);
                    }
                }
                if(top10List.size()==1){
                    MovieData movieData2 = top10List.get(0);
                    findUserWithSameTaste(movieData2,1);
                }
                else if (top10List.size()>1){
                    int random = new Random().nextInt(top10List.size());
                    int random2 = new Random().nextInt(top10List.size());
                    int random3 = new Random().nextInt(top10List.size());
                    int random4 = new Random().nextInt(top10List.size());
                    int random5 = new Random().nextInt(top10List.size());
                    MovieData movieData1 = top10List.get(random);
                    MovieData movieData2 = top10List.get(random2);
                    MovieData movieData3 = top10List.get(random3);
                    MovieData movieData4 = top10List.get(random4);

                    findUserWithSameTaste(movieData1,1);
                    findUserWithSameTaste(movieData2,2);
                    findUserWithSameTaste(movieData3,2);
                    findUserWithSameTaste(movieData4,2);
                }
                else if (top10List.size()==0){
                    getARandomGroupOfRandomPerson();
                    getARandomGroupOfRandomPerson();
                    getARandomGroupOfRandomPerson();
                    getARandomGroupOfRandomPerson();
//                    noContent.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public void findUserWithSameTaste(MovieData movie,int listNumber){


        Query query = FirebaseDatabase.getInstance().getReference().child("posts").orderByChild("id").equalTo(movie.getId()).limitToFirst(20);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<MovieData> sameMovieList1 =new ArrayList<>();

                if(snapshot.exists()){
                    for(DataSnapshot data:snapshot.getChildren()){
                        MovieData movieData = data.getValue(MovieData.class);
                        sameMovieList1.add(movieData);
                    }

                    if(sameMovieList1.size() !=0){
                        if (sameMovieList1.size()==1){
                            getARandomGroupOfRandomPerson();
                            return;
                        }else{
                            int random = new Random().nextInt(sameMovieList1.size());
                            MovieData movieData2 = sameMovieList1.get(random);
                            String publisherId = movieData2.getPublisherId();

                            while(publisherId.equals(firebaseUser.getUid())){
                                if(random ==0){
                                    random++;
                                }else{
                                   random--;
                                }
                                movieData2 = sameMovieList1.get(random);
                                publisherId = movieData2.getPublisherId();

                            }

                            if(publisherId!=null){
                                getMoviesOfRandomUser(publisherId, listNumber);
                            }
                        }
                        selectedMovieList.add(movie);
                    }else{

                    }
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    public void getMoviesOfRandomUser(String publisherId,int listNumber){

        int random = new Random().nextInt(2);
        String groupName;
        if (random==0){
            groupName = "rate";
        }else{
            groupName = "genre";
        }
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("movies").child(publisherId).child(groupName);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> nameOfGroupList = new ArrayList<>();

                int count = (int) (long)(snapshot.getChildrenCount());
                String selectedGroup="";
                if(snapshot.exists()){
                    for (DataSnapshot data : snapshot.getChildren()) {
                        nameOfGroupList.add(data.getKey());
                    }
                    int random = new Random().nextInt(count);
                    selectedGroup = nameOfGroupList.get(random);
                    selectedGroupList.add(selectedGroup);

                    databaseReference.child(selectedGroup).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                ArrayList<MovieData> moviesOfRandomUserList = new ArrayList<>();
                                for(DataSnapshot data:snapshot.getChildren()){
                                    MovieData movieData = data.getValue(MovieData.class);
                                    moviesOfRandomUserList.add(movieData);
                                    assert movieData != null;
                                }
                                arrayListOfWholeMovies.add(moviesOfRandomUserList);
                                numberOfRound++;
                                getUserInfo(publisherId,listNumber);
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
    private void getUserInfo(String publisherId,int listNumber){
        DatabaseReference databaseReference  = FirebaseDatabase.getInstance().getReference("users").child(publisherId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                if(getContext() == null){
                    return;
                }
                User user = snapshot.getValue(User.class);
                userList.add(user);
                Log.i(TAG, "onDataChange:arrayListOfWholeMovies "+arrayListOfWholeMovies.size());
                Log.i(TAG, "onDataChange:userList "+userList.size());
                Log.i(TAG, "onDataChange:selectedMovieList "+selectedMovieList.size());
                Log.i(TAG, "onDataChange:selectedGroupList "+selectedGroupList.size());

                if(userList.size() ==selectedGroupList.size() ) {
                    FollowAdapter followAdapter = new FollowAdapter(arrayListOfWholeMovies, userList, selectedMovieList, selectedGroupList, getContext(), followerNumber,currentUser);
                    recyclerView.setAdapter(followAdapter);
                }


                assert user != null;
//                Glide.with(getContext()).load(user.getImageUrl()).into(imageProfile);
//                userName.setText(user.getName());
//                fullName.setText(user.getName());
//                bio.setText(user.getName());
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


    }
    private void hideKeyboard(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);
    }
    public void getCurrentUser(){
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

    public void getARandomGroupOfRandomPerson(){
        Query query = FirebaseDatabase.getInstance().getReference("posts").limitToLast(15);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<MovieData> movies = new ArrayList<>();
                for (DataSnapshot data:snapshot.getChildren()){
                    MovieData movieData = data.getValue(MovieData.class);
                    movies.add(movieData);
                }
                int random = new Random().nextInt(15);
                MovieData movieData= movies.get(random);
                getMoviesOfRandomUser(movieData.getPublisherId(),4);
                movieData.setPosterUrl("https://firebasestorage.googleapis.com/v0/b/movie-257a9.appspot.com/o/random.jpg?alt=media&token=4aab01f2-c25d-4bc1-b5a6-43c19ce94448");
                selectedMovieList.add(movieData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}