package com.paradiso.movie.adaptor;

import android.annotation.SuppressLint;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PeopleWhoInsertAdapter extends RecyclerView.Adapter<PeopleWhoInsertAdapter.ViewHolder> {
    Context context;
    ArrayList<String> publisherIdList = new ArrayList<>();
    ArrayList<String> insertRateList = new ArrayList<>();
    User user;
    public PeopleWhoInsertAdapter(Context context, ArrayList<String> publisherIdList, ArrayList<String> insertRateList) {
        this.context = context;
        this.publisherIdList = publisherIdList;
        this.insertRateList = insertRateList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_people_who_insert,parent,false);
        return new PeopleWhoInsertAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.insertRated.setText(insertRateList.get(position));
        getPublisherInfo(holder.UsersImage,holder.publisher,publisherIdList.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = context.getApplicationContext().getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profile_id",publisherIdList.get(position));
                editor.apply();
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
            }
        });
    }


    @Override
    public int getItemCount() {
        return publisherIdList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView UsersImage;
        TextView publisher,insertRated;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            UsersImage = itemView.findViewById(R.id.user_image);
            publisher = itemView.findViewById(R.id.user_name);
            insertRated = itemView.findViewById(R.id.inserted_rate);
        }
    }

    private void getPublisherInfo(final ImageView imageProfile
            ,final TextView publisher,final String userId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                assert user != null;
                Glide.with(context).load(user.getImageUrl()).into(imageProfile);


                publisher.setText(user.getName());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
