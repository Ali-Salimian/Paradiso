package com.paradiso.movie.adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.paradiso.movie.R;
import com.paradiso.movie.model.MovieData;

import java.util.ArrayList;

public class WatchlistAdapter extends RecyclerView.Adapter<WatchlistAdapter.ViewHolder> {
    ArrayList<MovieData> watchlist;
    Context context;

    public WatchlistAdapter(ArrayList<MovieData> watchlist, Context context) {
        this.watchlist = watchlist;
        this.context = context;
    }

    @NonNull
    @Override
    public WatchlistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_watchlist,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WatchlistAdapter.ViewHolder holder, int position) {
        Glide.with(context.getApplicationContext()).load(watchlist.get(position).getPosterUrl()).into(holder.poster);
    }

    @Override
    public int getItemCount() {
        return watchlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView poster;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            poster= itemView.findViewById(R.id.poster_watchlist);
        }
    }
}
