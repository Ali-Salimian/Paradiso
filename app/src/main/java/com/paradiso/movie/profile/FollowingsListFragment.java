package com.paradiso.movie.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paradiso.movie.R;
import com.paradiso.movie.adaptor.UserAdapter;
import com.paradiso.movie.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class FollowingsListFragment extends Fragment {
    private static final String TAG = "FollowingsListFragment";
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;

    RecyclerView recyclerView;
    String profileId;
    ImageView backArrow;
    int snapshotSizeFollower=0;
    int numberOfRound = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_followings_list, container, false);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        backArrow = view.findViewById(R.id.backWard_following);



        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileId=prefs.getString("profile_id","none");

        recyclerView = view.findViewById(R.id.recycler_followers_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        getFollowersList();

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });
        return view;
    }
    public void getFollowersList(){
        ArrayList<User> followingsList = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Follow").child(profileId).child("following");
        numberOfRound =0;
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshotSizeFollower = (int) snapshot.getChildrenCount();

                for(DataSnapshot dat:snapshot.getChildren()){
                    numberOfRound++;
                    String id = dat.getKey();
                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("users").child(id);
                    databaseReference1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot1) {

                            User user = snapshot1.getValue(User.class);
                            followingsList.add(user);

                            if(numberOfRound == snapshotSizeFollower){
                                UserAdapter userAdapter =new UserAdapter(getContext(),followingsList);
                                recyclerView.setAdapter(userAdapter);
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