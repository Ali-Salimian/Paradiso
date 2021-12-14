package com.paradiso.movie.adaptor;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.paradiso.movie.model.User;
import com.paradiso.movie.profile.ProfileFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    Context mContext;
    ArrayList<User> usersList;
    User user;
    RecyclerViewClickListener listener ;

    public UserAdapter(Context mContext, ArrayList<User> usersList, User user,RecyclerViewClickListener listener) {
        this.mContext = mContext;
        this.usersList = usersList;
        this.user = user;
        this.listener = listener;
    }

    public UserAdapter(Context mContext, ArrayList<User> usersList) {
        this.mContext = mContext;
        this.usersList = usersList;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_caed_search,parent,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull UserAdapter.ViewHolder holder, int position) {
        holder.otherUsersName.setText(usersList.get(position).getUserName());
        Glide.with(mContext).asBitmap().load(usersList.get(position).getImageUrl()).into(holder.otherUsersImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profile_id",usersList.get(position).getId());
                editor.apply();


                ((FragmentActivity)v.getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).addToBackStack("UserAdapter").commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView otherUsersImage;
        TextView otherUsersName;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            otherUsersImage = itemView.findViewById(R.id.other_user_image_search);
            otherUsersName = itemView.findViewById(R.id.other_users_name_search);

            itemView.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {
            listener.onClick(view,getAdapterPosition());
        }
    }

    public interface RecyclerViewClickListener{
        void onClick(View view , int position);
    }
}
