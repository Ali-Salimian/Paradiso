package com.paradiso.movie.adaptor;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.paradiso.movie.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.ViewHolder>{

    ArrayList<String> genres = new ArrayList<>();
    Context mContext;
    ItemClickListener mItemClickListener;

    public GenreAdapter(ArrayList<String> genres, Context mContext, ItemClickListener mItemClickListener) {
        this.genres = genres;
        this.mContext = mContext;
        this.mItemClickListener = mItemClickListener;
    }

    public GenreAdapter(ArrayList<String> genres, Context mContext) {
        this.genres = genres;
        this.mContext = mContext;
    }

    @NonNull
    @NotNull
    @Override
    public GenreAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horiz_genre_cart,parent,false);


        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onBindViewHolder(@NonNull @NotNull GenreAdapter.ViewHolder holder, int position) {
        holder.genre_btn.setText(genres.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(genres.get(position));
            }
        });
        int darkFlag = mContext.getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        if(darkFlag == Configuration.UI_MODE_NIGHT_YES){
            holder.genre_btn.setForceDarkAllowed(false);
            holder.genre_btn.setBackgroundResource(R.drawable.button_border_black_dark_mode);
            holder.genre_btn.setTextColor(Color.WHITE);
        }


    }

    @Override
    public int getItemCount() {
        return genres.size();
    }

    public interface ItemClickListener{
        void onItemClick(String genre);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView genre_btn;



        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            genre_btn = itemView.findViewById(R.id.genre_btn);
        }
    }
}
