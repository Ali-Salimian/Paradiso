package com.paradiso.movie.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.paradiso.movie.R;
import com.paradiso.movie.model.ActorModel;
import com.paradiso.movie.model.MovieData;
import com.paradiso.movie.model.User;
import com.paradiso.movie.profile.ProfileFragment;
import com.paradiso.movie.search.SelectedMovieFragment;
import com.paradiso.movie.utils.MySpannable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MovieAdaptor extends RecyclerView.Adapter<MovieAdaptor.ViewHolder>  {
    private static final String TAG = "MOvie adapter";
    List<MovieData> movieDataList;
    Context mContext;
    FirebaseUser firebaseUser;
    int i;


    public MovieAdaptor(Context mContext, List<MovieData> movieDataList){
        this.mContext = mContext;
        this.movieDataList = movieDataList;



    }

    @NonNull
    @NotNull
    @Override
    public MovieAdaptor.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_view2,parent,false);;


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MovieAdaptor.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        MovieData movieData = movieDataList.get(position);
        i++;
        Log.i(TAG, "onBindViewHolder: iiii"+i);
        holder.title.setText(movieDataList.get(position).getName());
        Glide.with(mContext).asBitmap().load(movieDataList.get(position).getPosterUrl()).into(holder.posterHome);
        holder.imdbRate.setText(movieDataList.get(position).getImdbRate());
        holder.metaRate.setText(movieDataList.get(position).getMetaRate());
        holder.userRate.setText(movieDataList.get(position).getMyRate());
        holder.year.setText(movieDataList.get(position).getYear());
        String description_bold = "Description  ";
        String description_regular = movieDataList.get(position).getDescription();
        String finalString= "<b>"+description_bold+"</b> "+description_regular;
//        holder.description.setText(Html.fromHtml(finalString));
//        makeTextViewResizable(holder.description, 3, "More", true);


        String stringG = movieDataList.get(position).getGenre();
        String[] seperated = stringG.split(",");
        ArrayList<String> genreList = new ArrayList<>(Arrays.asList(seperated));
        GenreAdapter genreAdapter = new GenreAdapter(genreList,mContext);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(mContext.getApplicationContext(),RecyclerView.HORIZONTAL,false);
        holder.rec_genre.setLayoutManager(linearLayoutManager1);
        holder.rec_genre.setAdapter(genreAdapter);
        //
//
        String string =movieDataList.get(position).getDirector();
        ArrayList<String> directorList = new ArrayList<>();
        directorList.add(string);
        GenreAdapter directorAdapter = new GenreAdapter(directorList,mContext);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(mContext.getApplicationContext(),RecyclerView.HORIZONTAL,false);
        holder.rec_director.setLayoutManager(linearLayoutManager2);
        holder.rec_director.setAdapter(directorAdapter);
//
        ArrayList<String> actorNames = new ArrayList<>();
        ArrayList<ActorModel> actorModelList = movieDataList.get(position).getActors();
        if(movieDataList.get(position).getActors()!=null){
            for (int i = 0;i<actorModelList.size();i++){
                ActorModel actorModel = actorModelList.get(i);
                String actorName = actorModel.getName();
                actorNames.add(actorName);
            }
        }
        GenreAdapter actorAdapter = new GenreAdapter(actorNames,mContext);
        LinearLayoutManager linearLayoutManager3 = new LinearLayoutManager(mContext.getApplicationContext(),RecyclerView.HORIZONTAL,false);
        holder.rec_actor.setLayoutManager(linearLayoutManager3);
        holder.rec_actor.setAdapter(actorAdapter);

        getPublisherInfo(holder.userImage,holder.userName, holder.publisher,movieData.getPublisherId(),holder.userImageForRate);
        checkWatchlist(movieData,holder);
        holder.bookmark_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: tag"+holder.bookmark_image.getTag());
                if((Integer) holder.bookmark_image.getTag() == R.drawable.ic_bookmark_red) {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("watchlist")
                            .child(firebaseUser.getUid()).child(movieDataList.get(position).getId());
                    reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {

                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.i(TAG, "onComplete: herre");
                            holder.bookmark_image.setImageResource(R.drawable.ic_bookmark_border_red);
                            holder.bookmark_image.setTag(R.drawable.ic_bookmark_border_red);

                        }
                    });
                }else{
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("watchlist")
                            .child(firebaseUser.getUid()).child(movieDataList.get(position).getId());
                    reference.setValue(movieDataList.get(position)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            holder.bookmark_image.setImageResource(R.drawable.ic_bookmark_red);
                            holder.bookmark_image.setTag(R.drawable.ic_bookmark_red);
                        }


                    });
                }


            }
        });
        holder.posterHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectedMovieFragment selectedMovieFragment = new SelectedMovieFragment();
                Bundle bundle = new Bundle();
                bundle.putString("id",movieDataList.get(position).getId());
                bundle.putString("posterUrl",movieDataList.get(position).getPosterUrl());
                selectedMovieFragment.setArguments(bundle);

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedMovieFragment).addToBackStack("MovieAdapter").commit();
            }
        });
        holder.userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getApplicationContext().getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profile_id",movieDataList.get(position).getPublisherId());
                editor.apply();
                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).addToBackStack("MovieAdapter").commit();
            }
        });


    }

    @Override
    public int getItemCount() {
        return movieDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView posterHome,bookmarkHome,commentHome,userImage,userImageForRate,bookmark_image;
        public TextView title,imdbRate,metaRate,rottenRate,description,userName,publisher,userRate,year;
        RecyclerView rec_director,rec_genre,rec_actor;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            userName=itemView.findViewById(R.id.user_name);
            userImage= itemView.findViewById(R.id.userImage);
            posterHome = itemView.findViewById(R.id.poster_view_home);
            title = itemView.findViewById(R.id.title_home);
            imdbRate = itemView.findViewById(R.id.imdb_rate);
            metaRate= itemView.findViewById(R.id.meta_rate);
            rottenRate= itemView.findViewById(R.id.rot_rate);
            publisher = itemView.findViewById(R.id.publisher);
            userRate = itemView.findViewById(R.id.user_rate);
            userImageForRate = itemView.findViewById(R.id.user_image_foe_rate);
            year = itemView.findViewById(R.id.year_home);
            rec_director= itemView.findViewById(R.id.rec_director);
            rec_genre= itemView.findViewById(R.id.rec_genre);
            rec_actor= itemView.findViewById(R.id.rec_actor);
            description = itemView.findViewById(R.id.description_Text_view);
            bookmark_image = itemView.findViewById(R.id.bookmark_image_view_home);

        }
    }


    public static void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {

        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {

                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (maxLine == 0) {
                    int lineEndIndex = tv.getLayout().getLineEnd(0);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                    int lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else {
                    int lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, lineEndIndex, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                }
            }
        });

    }

    private static SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv,
                                                                            final int maxLine, final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(spanableText)) {


            ssb.setSpan(new MySpannable(false){
                @Override
                public void onClick(View widget) {
                    if (viewMore) {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, -1, "See Less", false);
                    } else {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, 3, ".. See More", true);
                    }
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);

        }
        return ssb;

    }


    private void getPublisherInfo(final ImageView imageProfile,final TextView userName
            ,final TextView publisher,final String userId,final  ImageView userImageForRate){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImageUrl()).into(imageProfile);
                Glide.with(mContext).load(user.getImageUrl()).into(userImageForRate);
                userName.setText(user.getName());
                publisher.setText(user.getName());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void checkWatchlist(MovieData movie,MovieAdaptor.ViewHolder holder){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("watchlist")
                .child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.child(movie.getId()).exists()){
                    holder.bookmark_image.setImageResource(R.drawable.ic_bookmark_red);
                    holder.bookmark_image.setTag(R.drawable.ic_bookmark_red);

                }else {
                    holder.bookmark_image.setImageResource(R.drawable.ic_bookmark_border_red);
                    holder.bookmark_image.setTag(R.drawable.ic_bookmark_border_red);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }


}
