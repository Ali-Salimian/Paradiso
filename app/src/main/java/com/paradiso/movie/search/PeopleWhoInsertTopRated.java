package com.paradiso.movie.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paradiso.movie.R;
import com.paradiso.movie.adaptor.PeopleWhoInsertAdapter;
import com.paradiso.movie.model.MovieData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class PeopleWhoInsertTopRated extends Fragment {

    RecyclerView recyclerView;
    String id;
    ImageView backArrow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=  inflater.inflate(R.layout.fragment_people_who_insert_top_rated, container, false);

        recyclerView = view.findViewById(R.id.recycler_who_insert_top_rate);
        assert getArguments() != null;
        id = getArguments().getString("id");
        backArrow = view.findViewById(R.id.backWard_topRated);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });
        Query query = FirebaseDatabase.getInstance().getReference("posts").orderByChild("id").equalTo(id).limitToLast(50);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                ArrayList<String> userList = new ArrayList<>();
                ArrayList<String> userRateList = new ArrayList<>();
                for (DataSnapshot data:snapshot.getChildren()){
                    MovieData movieData = data.getValue(MovieData.class);
                    assert movieData != null;
                    if(movieData.getSavedGroup().equals("91-100") || movieData.getSavedGroup().equals("81-90") ){
                        String publisherId = movieData.getPublisherId();
                        String userRate = movieData.getMyRate();
                        userList.add(publisherId);
                        userRateList.add(userRate);
                    }
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(linearLayoutManager);
                    PeopleWhoInsertAdapter peopleAdapter = new PeopleWhoInsertAdapter(getContext(),userList,userRateList);
                    recyclerView.setAdapter(peopleAdapter);



                }
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });


        return view;
    }
}