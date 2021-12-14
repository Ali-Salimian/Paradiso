package com.paradiso.movie.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.paradiso.movie.R;
import com.paradiso.movie.model.PopItemList;

import java.util.ArrayList;
import java.util.List;

public class PopListAdapter extends ArrayAdapter<PopItemList>  {
    private static final String TAG = "pop list adapter";
    private Context mContext;
    private ArrayList<String> groupName;
    private ArrayList<String> groupImage;
    int mResource;




    public PopListAdapter(@NonNull Context context, int resource, @NonNull List<PopItemList> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;

    }


    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String title = getItem(position).getTitle();
        String imageUrl = getItem(position).getImageUrl();
         PopItemList item = new PopItemList(title,imageUrl);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource,parent,false);

        TextView titleView = convertView.findViewById(R.id.title_popup);
        ImageView imageView = convertView.findViewById(R.id.poster_popup) ;

        titleView.setText(title);
        Glide.with(mContext).load(imageUrl).into(imageView);




        return convertView;
    }

}
