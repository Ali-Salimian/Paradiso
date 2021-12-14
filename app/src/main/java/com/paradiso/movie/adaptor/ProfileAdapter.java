package com.paradiso.movie.adaptor;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paradiso.movie.R;
import com.paradiso.movie.model.MovieData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {
    private static final String TAG = "ProfileAdapter";
    Context mContext;
    ArrayList<String> groupNameList;
    ArrayList<ArrayList<MovieData>> moviesList;
    ConstraintLayout cons;
    String profileId;
    String subGroup;
    FirebaseAuth mAuth=FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = mAuth.getCurrentUser();

    public ProfileAdapter(Context mContext, ArrayList<String> groupNameList, ArrayList<ArrayList<MovieData>> moviesList) {
        this.mContext = mContext;
        this.groupNameList = groupNameList;
        this.moviesList = moviesList;
    }
    public ProfileAdapter(Context mContext, ArrayList<String> groupNameList, ArrayList<ArrayList<MovieData>> moviesList, ConstraintLayout cons,String profileId,String subGroup) {
        this.mContext = mContext;
        this.groupNameList = groupNameList;
        this.moviesList = moviesList;
        this.cons = cons;
        this.profileId=profileId;
        this.subGroup = subGroup;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_profile,parent,false);
        return new ProfileAdapter.ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.groupName.setText(groupNameList.get(position));
        Log.i(TAG, "onBindViewHolder:groupNameList.get(position) "+groupNameList.get(position));
        Log.i(TAG, "onBindViewHolder:moviesList.get(position).size "+moviesList.size());

        HorizonAdapter horizonAdapter = new HorizonAdapter(moviesList.get(position),mContext,cons,profileId,1);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext.getApplicationContext(),RecyclerView.HORIZONTAL,false);
        holder.recyclerView.setLayoutManager(linearLayoutManager);
        holder.recyclerView.setAdapter(horizonAdapter);
        holder.deleteMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        int darkFlag = mContext.getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        if(darkFlag == Configuration.UI_MODE_NIGHT_YES){
            holder.parentCons.setForceDarkAllowed(false);
            holder.parentCons.setBackgroundResource(R.drawable.custom_dark_mode);
        }
    }

    @Override
    public int getItemCount() {
        return groupNameList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView groupName;
        ImageView deleteMenu;
        RecyclerView recyclerView;
        ConstraintLayout parentCons;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.group_name);
            recyclerView = itemView.findViewById(R.id.recycler_view_profile);
            deleteMenu = itemView.findViewById(R.id.delete_m);
            deleteMenu.setOnCreateContextMenuListener(this);
            parentCons = itemView.findViewById(R.id.constraintLayout_item_profile_parent);
            deleteMenu.setVisibility(View.GONE);


        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            menu.add(this.getAdapterPosition(),100,0,"remove the group");
        }
    }

    public void deleteMoviesOfAGroup(int position){


//        for(int i =0;i<moviesList.get(position).size();i++){
//            MovieData movieData = moviesList.get(position).get(i);
//            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("posts").child(movieData.getPostId());
//            databaseReference.removeValue();
//
//            DatabaseReference databaseRefRate = FirebaseDatabase.getInstance().getReference("movies")
//                    .child(firebaseUser.getUid()).child("rate").child(movieData.getSavedGroup()).child(movieData.getId());
//            databaseRefRate.removeValue();
//
//        }
//        DatabaseReference databaseRefGenre = FirebaseDatabase.getInstance().getReference("movies")
//                .child(firebaseUser.getUid()).child("genre").child(groupNameList.get(position));
//        databaseRefGenre.removeValue();

    }



}
