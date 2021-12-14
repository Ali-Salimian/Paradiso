package com.paradiso.movie.follower;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.paradiso.movie.R;
import com.paradiso.movie.adaptor.SelectedUserAdapter;
import com.paradiso.movie.model.User;
import com.paradiso.movie.profile.ProfileFragment;
import com.paradiso.movie.utils.DatabaseHelper;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.util.List;


public class FollowerFragment extends Fragment {
    private static final String TAG = "Follower";
    BottomNavigationView bottomNavigationView;

    Context mContext;
    FirebaseFirestore db ;
    FirebaseAuth mAuth;
    User user;
    RecyclerView recyclerView;
    DatabaseReference mDatabaseReference;
    FirebaseRecyclerAdapter<User, FindFriendsViewHolder> firebaseRecyclerAdapter;
    EditText searchBar;
    ImageView backArrow ;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=  inflater.inflate(R.layout.fragment_follower, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_search_followers);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        bottomNavigationView = view.findViewById(R.id.bottom_navigation);
        searchBar = view.findViewById(R.id.search_bar);
        backArrow = view.findViewById(R.id.back_arrow_follow);


        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });

        try {
            getSelectedUsers();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");
                searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SearchPeopleAndFriends(s.toString(),view);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }








    public void SearchPeopleAndFriends(String inputName,View view){
        Query searchPeopleAndFriendsQuery = mDatabaseReference.orderByChild("userName").
                startAt(inputName).endAt(inputName+"\uf8ff");

        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(searchPeopleAndFriendsQuery, User.class)
                        .build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<User, FindFriendsViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull @NotNull FindFriendsViewHolder holder, int i, @NonNull @NotNull User user) {
                holder.otherUsersName.setText(user.getName());
                Glide.with(getContext()).asBitmap().load(user.getImageUrl()).into(holder.otherUsersImage);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
                        boolean b = databaseHelper.addData(user);
                        SharedPreferences.Editor editor = getContext().getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                        editor.putString("profile_id",user.getId());
                        editor.apply();
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();

                    }
                });
            }

            @NonNull
            @NotNull
            @Override
            public FindFriendsViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.user_caed_search, parent, false);

                return new FindFriendsViewHolder(view);
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }
    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder {
        View mView;
        ImageView otherUsersImage;
        TextView otherUsersName;
        public FindFriendsViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            otherUsersImage = itemView.findViewById(R.id.other_user_image_search);
            otherUsersName = itemView.findViewById(R.id.other_users_name_search);
        }
    }
    public void getSelectedUsers() throws ParseException {
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        List<User> users = databaseHelper.getUsers();
        SelectedUserAdapter selectedUserAdapter = new SelectedUserAdapter(getContext(),users);
        recyclerView.setAdapter(selectedUserAdapter);
    }

}