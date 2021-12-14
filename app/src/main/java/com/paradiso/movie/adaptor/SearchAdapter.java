package com.paradiso.movie.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.paradiso.movie.R;
import com.paradiso.movie.model.PreDataMovies;
import com.paradiso.movie.search.SelectedMovieFragment;
import com.paradiso.movie.utils.DatabaseHelperMovie;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private static final String TAG = "SearchAdapter";
    ArrayList<PreDataMovies> preDataMoviesArrayList;
    PreDataMovies preDataMovies;
    Context mContext;
    private RecyclerViewClickListener listener;


    public SearchAdapter( Context mContext,ArrayList<PreDataMovies> preDataMoviesArrayList,RecyclerViewClickListener listener) {
        this.preDataMoviesArrayList = preDataMoviesArrayList;
        this.mContext = mContext;
        this.listener = listener;
    }

    public SearchAdapter(Context mContext,ArrayList<PreDataMovies> preDataMoviesArrayList) {
        this.preDataMoviesArrayList = preDataMoviesArrayList;
        this.mContext = mContext;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.search_cart,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SearchAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.tv_title.setText(preDataMoviesArrayList.get(position).getTitle());
        holder.tv_year.setText(preDataMoviesArrayList.get(position).getYear());
        Glide.with(mContext).asBitmap().load(preDataMoviesArrayList.get(position).getPoster()).into(holder.tv_Poster);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelperMovie databaseHelperMovie = new DatabaseHelperMovie(mContext.getApplicationContext());
                boolean b = databaseHelperMovie.addData(preDataMoviesArrayList.get(position));


                String id = preDataMoviesArrayList.get(position).getId();


                SelectedMovieFragment selectedMovieFragment = new SelectedMovieFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id",id);
                bundle.putString("posterUrl",preDataMoviesArrayList.get(position).getPoster());
                selectedMovieFragment.setArguments(bundle);

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedMovieFragment).addToBackStack("SearchAdapter").commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return preDataMoviesArrayList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView tv_Poster;
        TextView tv_title;
        TextView tv_Director;
        TextView tv_year;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tv_Poster = itemView.findViewById(R.id.poster_view);
            tv_title = itemView.findViewById(R.id.title_tv);

            tv_year = itemView.findViewById(R.id.yerat_tv);
        }
    }

    public interface RecyclerViewClickListener{
        void onClick(View view , int position);
    }

    public void clear() {
        int size = this.preDataMoviesArrayList.size();
        if (size > 0) {
            preDataMoviesArrayList.clear();
            this.notifyItemRangeRemoved(0, size);
        }
    }
}
