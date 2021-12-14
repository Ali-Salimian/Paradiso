package com.paradiso.movie.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.paradiso.movie.R;
import com.paradiso.movie.model.MovieData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class GroupPresentationAdapter extends RecyclerView.Adapter<GroupPresentationAdapter.ViewHolder> {

    ArrayList<MovieData> movieList = new ArrayList<MovieData>();
    Context mContext;
    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser firebaseUser;

    public GroupPresentationAdapter(ArrayList<MovieData> movieList, Context mContext) {
        this.movieList = movieList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_presentation,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(mContext).load(movieList.get(position).getPosterUrl()).into(holder.posterPresent);
        holder.title.setText(movieList.get(position).getName());
        holder.year.setText(movieList.get(position).getYear());
        holder.rate.setText(movieList.get(position).getImdbRate());

    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView posterPresent;
        TextView title,year,duration,rate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            posterPresent = itemView.findViewById(R.id.poster_present);
            title = itemView.findViewById(R.id.title_present);
            year = itemView.findViewById(R.id.year_present);
            rate = itemView.findViewById(R.id.rate_present);

        }
    }
}
