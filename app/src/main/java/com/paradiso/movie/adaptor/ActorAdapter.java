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
import com.paradiso.movie.model.ActorModel;

import java.util.ArrayList;

public class ActorAdapter extends RecyclerView.Adapter<ActorAdapter.ViewHolder>{
    ArrayList<ActorModel> actorList ;
    Context context;

    public ActorAdapter(ArrayList<ActorModel> actorList, Context context) {
        this.actorList = actorList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizon_actor_cart,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(actorList.get(position).getImageUrl()).into(holder.actorImage);
        holder.actorName.setText(actorList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return actorList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView actorImage;
        public TextView actorName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            actorImage = itemView.findViewById(R.id.actorImage_cart);
            actorName = itemView.findViewById(R.id.actor_name_cart);


        }
    }
}
