package com.paradiso.movie.search;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paradiso.movie.R;
import com.paradiso.movie.adaptor.GroupPresentationAdapter;
import com.paradiso.movie.model.MovieData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class FragmentGroupPresentation extends Fragment {
    private static final String TAG = "FragmentPresentation";
    RecyclerView recyclerView;
    TextView groupTitre;
    ArrayList<MovieData> moviesList = new ArrayList<>();
    FirebaseFirestore db ;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    ImageView backArrow;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_presentation, container, false);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser =mAuth.getCurrentUser();

        recyclerView = view.findViewById(R.id.rec_present);

        groupTitre = view.findViewById(R.id.title_group_cart_present);
        backArrow = view.findViewById(R.id.back_present_cart);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: fffffff");
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });



        getParentFragmentManager().setFragmentResultListener("data", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                String data1 = result.getString("fragmentGroup");
                groupTitre.setText(data1);
                getMovieData(data1);
            }
        });
        return view;
    }

    public void getMovieData(String groupName){
        ArrayList<MovieData> moviesList = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("categories").child(groupName);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot data:snapshot.getChildren()){
                    MovieData movieData = data.getValue(MovieData.class);
                    moviesList.add(movieData);

                }
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(linearLayoutManager);
                GroupPresentationAdapter presentationAdapter = new GroupPresentationAdapter(moviesList,getContext());
                recyclerView.setAdapter(presentationAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}