package com.paradiso.movie.search;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.paradiso.movie.R;
import com.paradiso.movie.adaptor.GridAdapter;
import com.paradiso.movie.adaptor.SearchAdapter;
import com.paradiso.movie.databinding.ActivityMainBinding;
import com.paradiso.movie.model.MovieData;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class SearchFragment extends Fragment {
    private static final String TAG = "SearchFragment";


    FirebaseUser firebaseUser;

    private SearchAdapter.RecyclerViewClickListener listener;

    TextView searchBar;


    ArrayList<MovieData> movies = new ArrayList<>();
    int i1 =0;


    TextView nameOfFollower ;
    ConstraintLayout constraintLayout_search;


    ActivityMainBinding binding;
    GridView gridView;


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pre_search_, container, false);

        searchBar = view.findViewById(R.id.search_bar);

        gridView = view.findViewById(R.id.grid_view);

        int darkFlag = getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        if(darkFlag == Configuration.UI_MODE_NIGHT_YES) {
            searchBar.setForceDarkAllowed(false);
            searchBar.setHintTextColor(Color.WHITE);
            searchBar.setBackgroundResource(R.drawable.custom_rounded_corner_white_dark_mode);
        }
        getGroupData();
        searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        ,new SearchMoviesFragment()).addToBackStack("gridAdapter").commit();
            }
        });



        return view;
    }

    public void getGroupData(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("categories");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int numberOfGroups=(int) snapshot.getChildrenCount();
                ArrayList<String> groupList = new ArrayList<>();
                ArrayList<ArrayList<String>> groupPosterList = new ArrayList<>();
                i1 = 0;

                for( DataSnapshot data:snapshot.getChildren()){

                    String groupName = data.getKey();
                    groupList.add(groupName);
                    assert groupName != null;
                    databaseReference.child(groupName).limitToFirst(4).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                ArrayList<String> posterList = new ArrayList<>();
                                i1++;
                                int i = 0;
                                for(DataSnapshot data:snapshot.getChildren()){
                                    i++;
                                    MovieData movieData = data.getValue(MovieData.class);
                                    assert movieData != null;
                                    String posterUrl = movieData.getPosterUrl();
                                    posterList.add(posterUrl);

                                    if(i==4){
                                        groupPosterList.add(posterList);

                                    }
                                }
                                if(i1 ==numberOfGroups && i ==4){
                                    GridAdapter gridAdapter = new GridAdapter(getContext(),groupList,groupPosterList);
                                    gridView.setAdapter(gridAdapter);
                                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            switch (position){
                                                case 0:
                                                    break;
                                                case 1:
                                            }
                                        }
                                    });

                                }

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

}