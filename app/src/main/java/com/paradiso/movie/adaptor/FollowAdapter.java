package com.paradiso.movie.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.paradiso.movie.R;
import com.paradiso.movie.model.MovieData;
import com.paradiso.movie.model.User;
import com.paradiso.movie.profile.ProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.ViewHolder> {
    private static final String TAG = "FollowAdapter";
    ArrayList<ArrayList<MovieData>> moviesList;
    ArrayList<User> userList;
    ArrayList<MovieData> selectedMovieList;
    ArrayList<String> selectedGroupList;
    Context context;
    User currentUser;
    int followerNumber;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


    public FollowAdapter(ArrayList<ArrayList<MovieData>> moviesList, ArrayList<User> userList,ArrayList<MovieData> selectedMovieList,ArrayList<String> selectedGroupList, Context context,int followerNumber,User currentUser) {
        this.moviesList = moviesList;
        this.userList = userList;
        this.context = context;
        this.selectedMovieList = selectedMovieList;
        this.selectedGroupList = selectedGroupList;
        this.followerNumber=followerNumber;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_follower,parent,false);
        return new FollowAdapter.ViewHolder(view);
    }

    @SuppressLint({"ResourceAsColor", "ClickableViewAccessibility"})
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Log.i(TAG, "onBindViewHolder: "+userList.size());
        Log.i(TAG, "onBindViewHolder: "+moviesList.size());


        Glide.with(context).load(selectedMovieList.get(position).getPosterUrl()).into(holder.selectedMoviePoster);
        Glide.with(context).load(userList.get(position).getImageUrl()).into(holder.selectedRandomUserImage);
        holder.selectedRandomUserName.setText(userList.get(position).getName());
        holder.selectedGroupTitle.setText(selectedGroupList.get(position));
        HorizonAdapter horizonAdapter = new HorizonAdapter(moviesList.get(position),context,moviesList.get(position).get(0).getId(),1);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context.getApplicationContext(),RecyclerView.HORIZONTAL,false);
        holder.recyclerView.setLayoutManager(linearLayoutManager);
        holder.recyclerView.setAdapter(horizonAdapter);

        checkFollow(position,holder.follow);

        int darkFlag = context.getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        if(darkFlag == Configuration.UI_MODE_NIGHT_YES){
            holder.follow.setForceDarkAllowed(false);
            holder.follow.setTextColor(R.color.red_darkMode_text);
            holder.parentCons.setForceDarkAllowed(false);
            holder.parentCons.setBackgroundResource(R.drawable.custom_dark_mode);
            holder.relContainPoster.setForceDarkAllowed(false);
            holder.relContainPoster.setBackgroundResource(R.drawable.custom_left_rounded_corner_dark_mode);

        }
        holder.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String btn = holder.follow.getText().toString();
                if(btn.equals("follow")){

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(userList.get(position).getId()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(userList.get(position).getId())
                            .child("followers").child(firebaseUser.getUid()).setValue(true);
                }else if(btn.equals("following")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(userList.get(position).getId()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(userList.get(position).getId())
                            .child("followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        holder.selectedRandomUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = context.getApplicationContext().getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profile_id",userList.get(position).getId());
                editor.apply();
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).addToBackStack("FollowerAdapter").commit();

            }
        });
        holder.selectedRandomUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = context.getApplicationContext().getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profile_id",userList.get(position).getId());
                editor.apply();
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).addToBackStack("FollowerAdapter").commit();

            }
        });





    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;
        ImageView selectedMoviePoster,selectedRandomUserImage;
        TextView selectedRandomUserName,selectedGroupTitle,follow;
        ConstraintLayout parentCons ;
        RelativeLayout relContainPoster;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            selectedMoviePoster = itemView.findViewById(R.id.selected_poster_of_movie);
            selectedRandomUserImage = itemView.findViewById(R.id.circleImageView);
            selectedRandomUserName = itemView.findViewById(R.id.name_of_follower);
            selectedGroupTitle = itemView.findViewById(R.id.selected_group);
            recyclerView = itemView.findViewById(R.id.searchedList);
            follow = itemView.findViewById(R.id.follow);
            parentCons = itemView.findViewById(R.id.first_lay_sel_group);
            relContainPoster = itemView.findViewById(R.id.relativeLayout_containPoster);

        }
    }
    private void checkFollow(int position, TextView follow){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.child(userList.get(position).getId()).exists()){
                    follow.setText("following");
                }else {
                    follow.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }



}
