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
import com.paradiso.movie.utils.DatabaseHelper;

import java.util.List;

public class SelectedUserAdapter extends RecyclerView.Adapter<SelectedUserAdapter.ViewHolder> {
    private static final String TAG = "SelectedUserAdapter";
    Context context;
    List<User> users;

    public SelectedUserAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_caed_search,parent,false);
        return new SelectedUserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.userName.setText(users.get(position).getName());
        Glide.with(context.getApplicationContext()).asBitmap().load(users.get(position).getImageUrl()).into(holder.userImage);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper databaseHelper = new DatabaseHelper(context);
                boolean b = databaseHelper.addData(users.get(position));
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profile_id",users.get(position).getId());
                editor.apply();

                ((FragmentActivity) v.getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView userImage;
        TextView userName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.other_user_image_search);
            userName = itemView.findViewById(R.id.other_users_name_search);
        }
    }
}
