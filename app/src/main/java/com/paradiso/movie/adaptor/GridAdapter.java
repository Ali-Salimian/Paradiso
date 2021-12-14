package com.paradiso.movie.adaptor;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.paradiso.movie.R;
import com.paradiso.movie.search.FragmentGroupPresentation;

import java.util.ArrayList;

public class GridAdapter extends BaseAdapter {
    private static final String TAG = "GridAdapter.class";
    Context context;
    ArrayList<String> groupName;
    ArrayList<ArrayList<String>> posterArrayList;
    ArrayList<String> onePosterList = new ArrayList<>();
    LayoutInflater layoutInflater;

    public GridAdapter(Context context, ArrayList<String> groupName, ArrayList<ArrayList<String>> posterArrayList) {
        this.context = context;
        this.groupName = groupName;
        this.posterArrayList = posterArrayList;
    }

    @Override
    public int getCount() {
        return groupName.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(layoutInflater ==null){
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if(convertView ==null){
            convertView = layoutInflater.inflate(R.layout.grid_item,null);
        }
        ImageView imageView1 = convertView.findViewById(R.id.grid_image_item1);
        ImageView imageView2 = convertView.findViewById(R.id.grid_image_item2);
        ImageView imageView3 = convertView.findViewById(R.id.grid_image_item3);
        TextView textView = convertView.findViewById(R.id.grid_item_name);
        onePosterList = posterArrayList.get(position);
        Glide.with(context).asBitmap().load(posterArrayList.get(position).get(0)).into(imageView1);
        Glide.with(context).asBitmap().load(posterArrayList.get(position).get(1)).into(imageView2);
        Glide.with(context).asBitmap().load(posterArrayList.get(position).get(2)).into(imageView3);

        textView.setText(groupName.get(position));


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle result = new Bundle();
                result.putString("fragmentGroup",groupName.get(position));
                ((FragmentActivity)context).getSupportFragmentManager().setFragmentResult("data",result);

                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        ,new FragmentGroupPresentation()).addToBackStack("gridAdapter").commit();
            }
        });


        return convertView;
    }
}
