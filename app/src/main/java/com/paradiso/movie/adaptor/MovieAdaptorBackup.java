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
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class MovieAdaptorBackup extends RecyclerView.Adapter  {
    private static final String TAG = "MovieAdaptorBackup";
    int followerNumber = 1;
    List<MovieData> movieDataList;
    Context mContext;
    FirebaseUser firebaseUser;

    User currentUser;

    ArrayList<ArrayList<MovieData>> arrayListOfWholeMovies= new ArrayList<>();
    ArrayList<User> userList = new ArrayList<>();
    ArrayList<MovieData> selectedMovieList= new ArrayList<>();
    ArrayList<String> selectedGroupList= new ArrayList<>();
    ShimmerFrameLayout shimmerFrameLayout;



    public MovieAdaptorBackup(Context mContext, List<MovieData> movieDataList){
        this.mContext = mContext;
        this.movieDataList = movieDataList;



    }

    @Override
    public int getItemViewType(int position) {
        if (position==5){

            return 1;
        }
        return 0;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view ;

        if(viewType ==1){
            view = layoutInflater.inflate(R.layout.cart_second_view_holder_movie_adapter,parent,false);
            return new ViewHolder2(view);
        }
        view = layoutInflater.inflate(R.layout.cart_view2,parent,false);
        return new ViewHolder1(view);

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {


        if(position==5){
            ViewHolder2 viewHolder2 = (ViewHolder2) holder;
            getMovieData(viewHolder2);
        }
        else {

            ViewHolder1 viewHolder1 = (ViewHolder1) holder;
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            MovieData movieData = movieDataList.get(position);


            viewHolder1.title.setText(movieDataList.get(position).getName());
            Glide.with(mContext).asBitmap().load(movieDataList.get(position).getPosterUrl()).into(viewHolder1.posterHome);
            viewHolder1.imdbRate.setText(movieDataList.get(position).getImdbRate());
            viewHolder1.metaRate.setText(movieDataList.get(position).getMetaRate());
            viewHolder1.userRate.setText(movieDataList.get(position).getMyRate());
            viewHolder1.year.setText(movieDataList.get(position).getYear());
            String description_bold = "Description  ";
            String description_regular = movieDataList.get(position).getDescription();
            String finalString = "<b>" + description_bold + "</b> " + description_regular;

            String stringG = movieDataList.get(position).getGenre();
            String[] seperated = stringG.split(",");
            ArrayList<String> genreList = new ArrayList<>(Arrays.asList(seperated));
            GenreAdapter genreAdapter = new GenreAdapter(genreList, mContext);
            LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(mContext.getApplicationContext(), RecyclerView.HORIZONTAL, false);
            viewHolder1.rec_genre.setLayoutManager(linearLayoutManager1);
            viewHolder1.rec_genre.setAdapter(genreAdapter);

            String string = movieDataList.get(position).getDirector();
            ArrayList<String> directorList = new ArrayList<>();
            directorList.add(string);
            GenreAdapter directorAdapter = new GenreAdapter(directorList, mContext);
            LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(mContext.getApplicationContext(), RecyclerView.HORIZONTAL, false);
            viewHolder1.rec_director.setLayoutManager(linearLayoutManager2);
            viewHolder1.rec_director.setAdapter(directorAdapter);
//
            ArrayList<String> actorNames = new ArrayList<>();
            ArrayList<ActorModel> actorModelList = movieDataList.get(position).getActors();
            if (movieDataList.get(position).getActors() != null) {
                for (int i = 0; i < actorModelList.size(); i++) {
                    ActorModel actorModel = actorModelList.get(i);
                    String actorName = actorModel.getName();
                    actorNames.add(actorName);
                }
            }
            GenreAdapter actorAdapter = new GenreAdapter(actorNames, mContext);
            LinearLayoutManager linearLayoutManager3 = new LinearLayoutManager(mContext.getApplicationContext(), RecyclerView.HORIZONTAL, false);
            viewHolder1.rec_actor.setLayoutManager(linearLayoutManager3);
            viewHolder1.rec_actor.setAdapter(actorAdapter);

            getPublisherInfo(viewHolder1.userImage, viewHolder1.userName, movieData.getPublisherId(), viewHolder1.userImageForRate);
            checkWatchlist(movieData, viewHolder1);
            viewHolder1.bookmark_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((Integer) viewHolder1.bookmark_image.getTag() == R.drawable.ic_bookmark_red) {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("watchlist")
                                .child(firebaseUser.getUid()).child(movieDataList.get(position).getId());
                        reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {

                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                viewHolder1.bookmark_image.setImageResource(R.drawable.ic_bookmark_border_red);
                                viewHolder1.bookmark_image.setTag(R.drawable.ic_bookmark_border_red);

                            }
                        });
                    } else {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("watchlist")
                                .child(firebaseUser.getUid()).child(movieDataList.get(position).getId());
                        reference.setValue(movieDataList.get(position)).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                viewHolder1.bookmark_image.setImageResource(R.drawable.ic_bookmark_red);
                                viewHolder1.bookmark_image.setTag(R.drawable.ic_bookmark_red);
                            }


                        });
                    }


                }
            });
            viewHolder1.posterHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SelectedMovieFragment selectedMovieFragment = new SelectedMovieFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("id", movieDataList.get(position).getId());
                    bundle.putString("posterUrl", movieDataList.get(position).getPosterUrl());
                    selectedMovieFragment.setArguments(bundle);

                    ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedMovieFragment).addToBackStack("MovieAdapter").commit();
                }
            });
            viewHolder1.userImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor editor = mContext.getApplicationContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("profile_id", movieDataList.get(position).getPublisherId());
                    editor.apply();
                    ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).addToBackStack("MovieAdapter").commit();
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return movieDataList.size();
    }

    public class ViewHolder1 extends RecyclerView.ViewHolder {
        public ImageView posterHome,bookmarkHome,commentHome,userImage,userImageForRate,bookmark_image;
        public TextView title,imdbRate,metaRate,rottenRate,description,userName,publisher,userRate,year;
        RecyclerView rec_director,rec_genre,rec_actor;

        public ViewHolder1(@NonNull @NotNull View itemView) {
            super(itemView);
            userName=itemView.findViewById(R.id.user_name);
            userImage= itemView.findViewById(R.id.userImage);
            posterHome = itemView.findViewById(R.id.poster_view_home);
            title = itemView.findViewById(R.id.title_home);
            imdbRate = itemView.findViewById(R.id.imdb_rate);
            metaRate= itemView.findViewById(R.id.meta_rate);
            rottenRate= itemView.findViewById(R.id.rot_rate);

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
    public class ViewHolder2 extends RecyclerView.ViewHolder {
        RecyclerView recyclerViewHolder2;
        public ViewHolder2(@NonNull View itemView) {
            super(itemView);
            recyclerViewHolder2 = itemView.findViewById(R.id.second_view_holder_recycler);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
            recyclerViewHolder2.setLayoutManager(linearLayoutManager);

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
            ,final String userId,final  ImageView userImageForRate){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImageUrl()).into(imageProfile);
                Glide.with(mContext).load(user.getImageUrl()).into(userImageForRate);
                userName.setText(user.getName());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void checkWatchlist(MovieData movie, MovieAdaptorBackup.ViewHolder1 holder){
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








    public void getMovieData(ViewHolder2 viewHolder2){
        ArrayList<MovieData> movies = new ArrayList<>();


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("movies").child(firebaseUser.getUid()).child("rate");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap:snapshot.getChildren()){
                    if(Objects.equals(snap.getKey(), "91-100") || Objects.equals(snap.getKey(), "81-90")){
                        for(DataSnapshot data:snap.getChildren()){
                            MovieData movieData = data.getValue(MovieData.class);
                            assert movieData != null;
                            movies.add(movieData);
                        }
                    }
                }

                if(movies.size()==1){
                    MovieData movieData2 = movies.get(0);
                    findUserWithSameTaste(movieData2,1,viewHolder2);
                }
                else if (movies.size()>1){
                    int random = new Random().nextInt(movies.size());
                    MovieData movieData1 = movies.get(random);
                    findUserWithSameTaste(movieData1,1,viewHolder2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }


    public void findUserWithSameTaste(MovieData movie,int listNumber,ViewHolder2 viewHolder2){
//        IT IS REALLY IMPORTANT THE ID LOCATION AN NAME SHOULD CHANGE IN MOVIE DATA CLASS
        Query query = FirebaseDatabase.getInstance().getReference().child("posts").orderByChild("id").equalTo(movie.getId()).limitToFirst(50);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<MovieData> sameMovieList1 =new ArrayList<>();

                if(snapshot.exists()){

                    for(DataSnapshot data:snapshot.getChildren()){
                        MovieData movieData = data.getValue(MovieData.class);
                        assert movieData != null;
                        if(movieData.getSavedGroup().equals("91-100") || movieData.getSavedGroup().equals("81-90") ){
                            sameMovieList1.add(movieData);
                        }

                    }

                    if(sameMovieList1.size() !=0){
                        if (sameMovieList1.size()==1){
                            return;
                        }else{
                            int random = new Random().nextInt(sameMovieList1.size());
                            MovieData movieData2 = sameMovieList1.get(random);
                            String publisherId = movieData2.getPublisherId();

                            while(publisherId.equals(firebaseUser.getUid())){
                                if(random ==0){
                                    random++;
                                }else{
                                    random--;
                                }
                                movieData2 = sameMovieList1.get(random);
                                publisherId = movieData2.getPublisherId();

                            }
                            if(publisherId!=null){
                                getMoviesOfRandomUser(publisherId, listNumber,viewHolder2);
                            }
                        }
                        selectedMovieList.add(movie);
                    }
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    public void getMoviesOfRandomUser(String publisherId,int listNumber,ViewHolder2 viewHolder2){

        int random = new Random().nextInt(2);
        String groupName;
        if (random==0){
            groupName = "rate";
        }else{
            groupName = "genre";
        }
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("movies").child(publisherId).child(groupName);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> nameOfGroupList = new ArrayList<>();

                int count = (int) (long)(snapshot.getChildrenCount());
                String selectedGroup="";
                if(snapshot.exists()){
                    for (DataSnapshot data : snapshot.getChildren()) {
                        nameOfGroupList.add(data.getKey());
                    }
                    int random = new Random().nextInt(count);
                    selectedGroup = nameOfGroupList.get(random);
                    selectedGroupList.add(selectedGroup);
                    databaseReference.child(selectedGroup).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                ArrayList<MovieData> moviesOfRandomUserList = new ArrayList<>();
                                for(DataSnapshot data:snapshot.getChildren()){
                                    MovieData movieData = data.getValue(MovieData.class);
                                    moviesOfRandomUserList.add(movieData);
                                    assert movieData != null;
                                }
                                arrayListOfWholeMovies.add(moviesOfRandomUserList);

                                getUserInfo(publisherId,listNumber,viewHolder2);
                                getCurrentUser();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getUserInfo(String publisherId,int listNumber,ViewHolder2 viewHolder2){
        DatabaseReference databaseReference  = FirebaseDatabase.getInstance().getReference("users").child(publisherId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);
                userList.add(user);
                Log.i(TAG, "onDataChange:arrayListOfWholeMovies "+arrayListOfWholeMovies.size());
                Log.i(TAG, "onDataChange:userList "+userList.size());
                Log.i(TAG, "onDataChange:selectedMovieList "+selectedMovieList.size());
                Log.i(TAG, "onDataChange:selectedGroupList "+selectedGroupList.size());


                if(userList.size() ==selectedGroupList.size() ) {
                    FollowAdapter followAdapter = new FollowAdapter(arrayListOfWholeMovies, userList, selectedMovieList, selectedGroupList, mContext, followerNumber,currentUser);
                    viewHolder2.recyclerViewHolder2.setAdapter(followAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


    }
    public void getCurrentUser(){
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid());
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUser = snapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
