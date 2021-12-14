package com.paradiso.movie.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.paradiso.movie.R;
import com.paradiso.movie.model.MovieData;
import com.paradiso.movie.search.SelectedMovieFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class HorizonAdapter extends RecyclerView.Adapter<HorizonAdapter.ViewHolder> {
    private static final String TAG ="Horizon Adapter " ;
    ArrayList<MovieData> movies = new ArrayList<>();
    Context mContext;
    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser firebaseUser;
    int watchlistNumber;

    ConstraintLayout c;
    Fragment currentFragment;
    String profileId;
    PopupWindow popupWindow;


    public HorizonAdapter(ArrayList<MovieData> movies, Context mContext, String profileId,int watchlistNumber) {
        this.movies = movies;
        this.mContext = mContext;
        this.profileId=profileId;
        this.watchlistNumber = watchlistNumber;
    }
    public HorizonAdapter(ArrayList<MovieData> movies, Context mContext, ConstraintLayout c,String profileId,int watchlistNumber) {
        this.movies = movies;
        this.mContext = mContext;
        this.c = c;
        this.profileId=profileId;
        this.watchlistNumber = watchlistNumber;

    }




    @NotNull
    @Override
    public HorizonAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizon_recycler_cart,parent,false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onBindViewHolder(@NonNull @NotNull HorizonAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        Glide.with(mContext).asBitmap().load(movies.get(position).getPosterUrl()).into(holder.poster_cart_search_h);
        holder.title_cart_search_h.setText(movies.get(position).getName());
        holder.userRate_h.setText(movies.get(position).getMyRate());
        holder.imdb_cart_rate_h.setText(movies.get(position).getImdbRate());
        holder.year_cart_search_h.setText(movies.get(position).getYear());

        if(firebaseUser.getUid().equals(profileId)){
            if(watchlistNumber == 0){
                turnWatchlistToRemove(holder);
            }else{
                turnWatchlistToEdit(holder);
            }

        }else if(!firebaseUser.getUid().equals(profileId)) {

            checkWatchlist(movies.get(position),holder);
        }

        int darkFlag = mContext.getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        if(darkFlag == Configuration.UI_MODE_NIGHT_YES){
            holder.parentCons.setForceDarkAllowed(false);
            holder.parentCons.setBackgroundResource(R.drawable.custom_rounded_corner_white_dark_mode);
            holder.addToWatchlist.setForceDarkAllowed(false);
            holder.addToWatchlist.setBackgroundResource(R.drawable.button_border_red_dark_mode);
            holder.addToWatchlist.setTextColor(Color.WHITE);
        }

        holder.addToWatchlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btn = holder.addToWatchlist.getText().toString();
                if(btn.equals("+Watchlist")){
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("watchlist").child(firebaseUser.getUid()).child(movies.get(position).getId());
                    databaseReference.setValue(movies.get(position)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(v.getContext(), movies.get(position).getName()+"added to watchlist",Toast.LENGTH_SHORT).show();
                        }
                    });
                }else if(btn.equals("Added to watchlist")){
                    Toast.makeText(v.getContext(), movies.get(position).getName()+"has already been added to watchList",Toast.LENGTH_SHORT).show();

                }else if(btn.equals("Edit")){
                    showPopupWindow(movies.get(position));
                }
                else if(btn.equals("Remove")){
                    RemoveFromWatchlist(movies.get(position));
                    notifyDataSetChanged();
                }


            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SelectedMovieFragment selectedMovieFragment = new SelectedMovieFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id",movies.get(position).getId());
                bundle.putString("posterUrl",movies.get(position).getPosterUrl());
                selectedMovieFragment.setArguments(bundle);

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedMovieFragment).addToBackStack("HorizonAdapter").commit();

            }
        });



//        getUserData(holder,movies,position);
    }

//    private void getUserData(HorizonAdapter.ViewHolder holder,ArrayList<MovieData> movie,int position) {
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(movie.get(position).getPublisherId());
//        databaseReference.
//    }

    public void turnWatchlistToEdit(ViewHolder holder){
        holder.addToWatchlist.setText("Edit");
    }
    public void turnWatchlistToRemove(ViewHolder holder){
        holder.addToWatchlist.setText("Remove");
    }


    private void checkWatchlist(MovieData movie,HorizonAdapter.ViewHolder holder){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("watchlist")
                .child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.child(movie.getId()).exists()){
                    holder.addToWatchlist.setText("Added to watchlist");

                }else {
                    holder.addToWatchlist.setText("+Watchlist");
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }
    @Override
    public int getItemCount() {
        return movies.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView poster_cart_search_h,user_image_cart_h;
        public TextView title_cart_search_h,imdb_cart_rate_h,year_cart_search_h,userRate_h;
        public Button addToWatchlist;
        ConstraintLayout parentCons;


        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            poster_cart_search_h = itemView.findViewById(R.id.poster_cart_search_h);
            title_cart_search_h = itemView.findViewById(R.id.title_cart_search_h);
            imdb_cart_rate_h=itemView.findViewById(R.id.imdb_cart_rate_h);
            year_cart_search_h = itemView.findViewById(R.id.year_cart_search_h);
            userRate_h = itemView.findViewById(R.id.user_cart_rate_h);
            user_image_cart_h = itemView.findViewById(R.id.image_user_rate_h);
            addToWatchlist = itemView.findViewById(R.id.watchList_btn);
            parentCons = itemView.findViewById(R.id.constraintLayout_horizon);



        }

    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void showPopupWindow(MovieData movieData){
//        LayoutInflater layoutInflater = (LayoutInflater) mContext.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = LayoutInflater.from(mContext.getApplicationContext()).inflate(R.layout.popup_window_profile,null);

        ImageView cancel_btn=popupView.findViewById(R.id.cancel_image_pop);
        TextView edit_textView = popupView.findViewById(R.id.edit_text_view_profile);
        TextView remove_textView = popupView.findViewById(R.id.remove_text_view_profile);
        androidx.appcompat.widget.Toolbar toolbar = popupView.findViewById(R.id.toolbar);
        TextView titre = popupView.findViewById(R.id.titre_pop);
        popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT,true);
        int darkFlag = mContext.getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        if(darkFlag == Configuration.UI_MODE_NIGHT_YES){
            edit_textView.setForceDarkAllowed(false);
            edit_textView.setBackgroundResource(R.drawable.custom_dark_mode);
            edit_textView.setTextColor(Color.WHITE);
            remove_textView.setForceDarkAllowed(false);
            remove_textView.setBackgroundResource(R.drawable.custom_dark_mode);
            remove_textView.setTextColor(Color.WHITE);
            toolbar.setForceDarkAllowed(false);
            toolbar.setBackgroundResource(R.drawable.custom_dark_mode);
            remove_textView.setTextColor(Color.WHITE);
            titre.setForceDarkAllowed(false);
            titre.setBackgroundResource(R.drawable.custom_dark_mode);
            titre.setTextColor(Color.WHITE);


        }
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        edit_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectedMovieFragment selectedMovieFragment = new SelectedMovieFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id",movieData.getId());
                bundle.putString("posterUrl",movieData.getPosterUrl());
                bundle.putBoolean("edit",true);
                selectedMovieFragment.setArguments(bundle);
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedMovieFragment).commit();
                popupWindow.dismiss();
            }
        });

        remove_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("movies").child(firebaseUser.getUid()).child("rate").child(movieData.getSavedGroup()).child(movieData.getId());
                firebaseDatabase.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@io.reactivex.rxjava3.annotations.NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()){
                            DatabaseReference firebaseDatabase1 = FirebaseDatabase.getInstance().getReference("movies").child(firebaseUser.getUid()).child("genre").child(movieData.getSavedTitleGroup()).child(movieData.getId());
                            firebaseDatabase1.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("posts").child(movieData.getPostId());
                                        databaseRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@androidx.annotation.NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(mContext.getApplicationContext(),"The movie is removed",Toast.LENGTH_SHORT).show();

                                                }else{
                                                    Toast.makeText(mContext.getApplicationContext(),"The movie is NOT saved",Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }
                            });


                        }else{
                            Toast.makeText(mContext.getApplicationContext(),"The movie is NOT saved",Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@io.reactivex.rxjava3.annotations.NonNull @NotNull Exception e) {
                        Toast.makeText(mContext.getApplicationContext(),"The movie is NOT saved",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        popupWindow.showAtLocation(c, Gravity.BOTTOM,0,0);
//        popupWindow.showAsDropDown(popupView, 0, 0);

    }

    public void RemoveFromWatchlist(MovieData movieData){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("watchlist")
                .child(firebaseUser.getUid()).child(movieData.getId());
        reference.removeValue();
    }


}
