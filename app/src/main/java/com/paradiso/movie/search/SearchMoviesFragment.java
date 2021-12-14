package com.paradiso.movie.search;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.paradiso.movie.R;
import com.paradiso.movie.adaptor.SearchAdapter;
import com.paradiso.movie.model.MovieData;
import com.paradiso.movie.model.PreDataMovies;
import com.paradiso.movie.utils.DatabaseHelperMovie;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class SearchMoviesFragment extends Fragment {
    private static final String TAG = "SearchMoviesFragment";

    BottomNavigationView bottomNavigationView;
    MovieData movieData;
    FirebaseFirestore db ;
    FirebaseAuth mAuth;

    ArrayList<PreDataMovies> searchedList;
    SearchAdapter adapter ;

    private SearchAdapter.RecyclerViewClickListener listener;
    GetID getID = new GetID();
    RecyclerView recyclerView1;
    EditText searchBar;
    ImageView backArrow;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_search_movies, container, false);

        bottomNavigationView = view.findViewById(R.id.bottom_navigation);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        searchedList = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        recyclerView1 =view.findViewById(R.id.recycler_view_search_movies);
        recyclerView1.setLayoutManager(linearLayoutManager);

        getSelectedMovie();

        searchBar = view.findViewById(R.id.search_bar);
        backArrow = view.findViewById(R.id.back_arrow_search);

        searchBar.setActivated(true);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        ,new SearchFragment()).commit();
            }
        });
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                GetID getID = new GetID();
                getID.execute("https://imdb-api.com/en/API/SearchMovie/k_lfd0fp87/"+s);
//                getID.execute("https://movie-database-imdb-alternative.p.rapidapi.com/?s=no%20time%20to%20die&r=json&page=1");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });





        return view;
    }


    public class GetID extends AsyncTask<String,Void,String> {



        @Override
        protected String doInBackground(String... urls) {
            String result="";
            URL url;

            HttpURLConnection urlCon = null;

            try {
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url(urls[0])
                        .get()
                        .build();

                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }


//            try {
//
//                OkHttpClient client = new OkHttpClient();
//
//                Request request = new Request.Builder()
//                        .url("https://movie-database-imdb-alternative.p.rapidapi.com/?s=no%20time%20to%20die&r=json&page=1")
//                        .get()
//                        .addHeader("x-rapidapi-host", "movie-database-imdb-alternative.p.rapidapi.com")
//                        .addHeader("x-rapidapi-key", "fa0754ef80msh978d3affb8e87eep19f809jsn6a6ab6bdfe13")
//                        .build();
//
//                Response response = client.newCall(request).execute();
////                url = new URL(urls[0]);
////                urlCon = (HttpURLConnection) url.openConnection();
//
//
////                urlCon.setRequestProperty("x-rapidapi-host", "movie-database-imdb-alternative.p.rapidapi.com");
////                urlCon.setRequestProperty("x-rapidapi-key", "fa0754ef80msh978d3affb8e87eep19f809jsn6a6ab6bdfe13");
////                urlCon.setRequestMethod("GET");
////                urlCon.setRequestProperty("Content-Language", "en-US");
////                urlCon.setUseCaches(false);
////                urlCon.setDoInput(true);
////                urlCon.setDoOutput(true);
////                Log.i(TAG, "doInBackground: "+urlCon.getRequestProperty("x-rapidapi-key").toString());
////                Log.i(TAG, "doInBackground: "+urlCon.toString());
////
////                InputStream inputStream = urlCon.getInputStream();
////                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
////                int data = inputStreamReader.read();
////                Log.i(TAG, "doInBackground: "+data);
////                while (data != -1){
////                    char current = (char) data;
////                    result += current;
////                    data = inputStreamReader.read();
////
////                }
//
//
//                return response.body().string();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            return null;


        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            clearRecycler();




            try {

                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("results");
                for(int i=0 ; i < jsonArray.length();i++){
                    JSONObject jObject = jsonArray.getJSONObject(i);
                    String imageUrl = jObject.getString("image");
                    String title = jObject.getString("title");
                    String year = jObject.getString("description");
                    String id = jObject.getString("id");


                    PreDataMovies preDataMovies = new PreDataMovies(id,imageUrl,title,year);
                    searchedList.add(preDataMovies);
                }


                adapter = new SearchAdapter(getContext(),searchedList,listener);
                recyclerView1.setAdapter(adapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    public void clearRecycler(){
        if(searchedList.size()>0) {
            searchedList.clear();
            adapter.notifyDataSetChanged();
        }
    }

    public void getSelectedMovie(){
        DatabaseHelperMovie databaseHelperMovie = new DatabaseHelperMovie(getContext());
        ArrayList<PreDataMovies> movies = databaseHelperMovie.getMovies();
        SearchAdapter searchAdapter = new SearchAdapter(getContext(),movies);
        recyclerView1.setAdapter(searchAdapter);
    }
}