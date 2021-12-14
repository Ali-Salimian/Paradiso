package com.paradiso.movie.home;

import android.animation.ValueAnimator;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.paradiso.movie.R;
import com.paradiso.movie.adaptor.MovieAdaptorBackup;
import com.paradiso.movie.adaptor.WatchlistAdapter;
import com.paradiso.movie.model.MovieData;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;


public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    String url = "https://imdb-api.com/en/API/ComingSoon/k_lfd0fp87";
    MovieTask movieTask = new MovieTask();

    private AdView mAdView;
    ArrayList<MovieData> movieDataList = new ArrayList<>();
    ArrayList<MovieData> movieDataList1 = new ArrayList<>();
    FirebaseFirestore db;
    FirebaseAuth mAuth ;
    FirebaseUser firebaseUser;
    ArrayList<String> followingList;
    RecyclerView recyclerView;
    RecyclerView watchRecycler;
    TextView watchListText;

    ProgressBar homeProgress;
    LinearLayout transLinear;
    MovieAdaptorBackup movieAdaptor;
    NestedScrollView mainScrollView;
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

//        movieTask.execute(url);
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        watchListText = view.findViewById(R.id.textView_watchlist);
        homeProgress = view.findViewById(R.id.progress_home);
        transLinear = view.findViewById(R.id.trans_lin);
        mainScrollView = view.findViewById(R.id.nestedScroll_parent);
        MobileAds.initialize(getActivity());
        mAdView = view.findViewById(R.id.adView);

//        bannerAd();



        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        firebaseUser =mAuth.getCurrentUser();
        checkFollowing();
        getWatchlist();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView = view.findViewById(R.id.recycler_view_home);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);

        watchRecycler = view.findViewById(R.id.watchList_recycler);

        watchListText.bringToFront();

        int darkFlag = getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        if(darkFlag == Configuration.UI_MODE_NIGHT_YES){
            watchListText.setForceDarkAllowed(false);
            watchListText.setBackgroundResource(R.drawable.button_border_black_dark_mode);
        }

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1f, 0f);
        valueAnimator.setDuration(11000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float alpha = (float) animation.getAnimatedValue();
                watchListText.setAlpha(alpha);
            }
        });
        valueAnimator.start();
        return view;
    }


    public void getMovieData(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("movies")
                .child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data:snapshot.getChildren()){
                    MovieData movieData = data.getValue(MovieData.class);
                    movieDataList1.add(movieData);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),"The movie is NOT received",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void checkFollowing(){
        followingList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(firebaseUser.getUid())
                .child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingList.clear();
                for(DataSnapshot data:snapshot.getChildren()){
                    followingList.add(data.getKey());
                }
                if(followingList.size()!=0){
                    readPosts();
                }else{
                    readRandomPosts();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void readPosts(){
        Query databaseReference = FirebaseDatabase.getInstance().getReference("posts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                movieDataList.clear();
                for(DataSnapshot data:snapshot.getChildren()){
                    MovieData movieData = data.getValue(MovieData.class);
                    for(String id:followingList){
                        assert movieData != null;
                        if(movieData.getPublisherId().equals(id)){
                            movieDataList.add(movieData);
                            Collections.reverse(movieDataList);
                        }
                    }
                }
                recyclerView.setVisibility(View.VISIBLE);

                watchRecycler.setVisibility(View.VISIBLE);
                movieAdaptor = new MovieAdaptorBackup(getContext(),movieDataList);
                recyclerView.setAdapter(movieAdaptor);
                transLinear.setVisibility(View.GONE);
                homeProgress.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


    }
    public void getWatchlist(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("watchlist").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<MovieData> watchlist = new ArrayList<>();
                if (!snapshot.exists()){
                    watchListText.setVisibility(View.GONE);
                }
                for (DataSnapshot data:snapshot.getChildren()){
                    MovieData movieData = data.getValue(MovieData.class);
                    watchlist.add(movieData);
                }
                WatchlistAdapter watchlistAdapter = new WatchlistAdapter(watchlist,getContext());
                LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
                watchRecycler.setLayoutManager(linearLayoutManager1);
                watchRecycler.setAdapter(watchlistAdapter);

                LinearSnapHelper snapHelper = new LinearSnapHelper();
                recyclerView.setOnFlingListener(null);
                snapHelper.attachToRecyclerView(recyclerView);
                if (watchlist.size() != 0){
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if(linearLayoutManager1.findLastCompletelyVisibleItemPosition() <(watchlistAdapter.getItemCount()-1) ){
                                watchRecycler.smoothScrollBy(5,0);

//                            linearLayoutManager1.smoothScrollToPosition(watchRecycler,new RecyclerView.State(),linearLayoutManager1.findLastCompletelyVisibleItemPosition()+1);
                            }else if(linearLayoutManager1.findLastCompletelyVisibleItemPosition() ==(watchlistAdapter.getItemCount()-1) ) {
                                linearLayoutManager1.smoothScrollToPosition(watchRecycler, new RecyclerView.State(), 0);
                            }

                        }
                    },0,100);
                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void bannerAd(){
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }
        });

    }
    private void readRandomPosts(){
        Query databaseReference = FirebaseDatabase.getInstance().getReference("posts").limitToLast(50);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                movieDataList.clear();
                for(DataSnapshot data:snapshot.getChildren()){
                    MovieData movieData = data.getValue(MovieData.class);
                    movieDataList.add(movieData);
                    Collections.reverse(movieDataList);

                }
                MovieAdaptorBackup movieAdaptor = new MovieAdaptorBackup(getContext(),movieDataList);
                recyclerView.setAdapter(movieAdaptor);
                transLinear.setVisibility(View.GONE);
                homeProgress.setVisibility(View.GONE);
//                movieAdaptor.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public class MovieTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {
            String result="";
            URL url;
            HttpURLConnection urlCon = null;

            try {
                url = new URL(urls[0]);
                urlCon = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlCon.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                int data = inputStreamReader.read();
                while (data != -1){
                    char current = (char) data;
                    result += current;
                    data = inputStreamReader.read();

                }
                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return String.valueOf(e);
            } catch (IOException e) {
                e.printStackTrace();
                return String.valueOf(e);
            }



        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {


                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray =  jsonObject.getJSONArray("items");
                for (int i =0;i<jsonArray.length();i++){
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    String title = jsonObject1.getString("title");
                    String id = jsonObject1.getString("id");
                    String posterUrl = jsonObject1.getString("image");
                    String year = jsonObject1.getString("year");
                    String imdbRate = jsonObject1.getString("imDbRating");

                    MovieData movieData;

                    movieData = new MovieData(id,title,year,posterUrl,imdbRate);

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("categories").child("ComingSoon").child(movieData.getId());
                    databaseReference.setValue(movieData);
                }



            } catch (JSONException e) {
                Log.i(TAG, "onPostExecute: e6"+e);
                e.printStackTrace();
            }

        }
    }



}