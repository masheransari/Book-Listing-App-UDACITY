package com.example.asheransari.bookapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Context mContext;
    private BookAdapter mBookAdapter;
    public static final String LOG_TAG = MainActivity.class.getName();
    private static final String BOOK_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    ArrayList<Book> booksList ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null ||!savedInstanceState.containsKey("keyBookLost"))
        {
            booksList = new ArrayList<>();
        }
        else
        {
            booksList = savedInstanceState.getParcelableArrayList("keyBookList");
        }

        final EditText inputString = (EditText)findViewById(R.id.edit_text_view);

        Button btn = (Button)findViewById(R.id.searchBtn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String search = inputString.getText().toString().replace("","+");
                String url = (BOOK_REQUEST_URL + search);

                if (isOnline(getApplicationContext()))
                {
                    BookAsyncTask task= new BookAsyncTask();
                    task.execute(url);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "No Internet Connection",Toast.LENGTH_LONG).show();
                }
            }
        });
        ListView bookListView = (ListView)findViewById(R.id.list);
        bookListView.setEmptyView(findViewById(R.id.empty_list_view));
        mBookAdapter = new BookAdapter(this, booksList);
        bookListView.setAdapter(mBookAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.e(LOG_TAG, "In onSavedInstanceState().......");
        outState.putParcelableArrayList("keyBookList", booksList);
        super.onSaveInstanceState(outState);
    }

    public boolean isOnline(Context c)
    {
//        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo info = cm.getActiveNetworkInfo();
//        boolean isConnected = info !=null && info.isConnectedOrConnecting();
//        return isConnected;
        ConnectivityManager cm =
                (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    private class BookAsyncTask extends AsyncTask<String, Void, ArrayList<Book>>
    {


        @Override
        protected ArrayList<Book> doInBackground(String... urls) {
            URL url = CreateUrl(urls[0]);
            String jSonResponce="";

            try
            {
                jSonResponce = makeHttpRequest(url);
            }
            catch (IOException e)
            {
                Log.e(LOG_TAG, "in DoInBackground()....,", e);
            }
            ArrayList<Book> books = extractFeatureFromJson(jSonResponce);
            return books;
        }

        @Override
        protected void onPostExecute(ArrayList<Book> books) {
            mBookAdapter.clear();
            if (books != null)
            {
                mBookAdapter.addAll(books);
            }
            Log.e(LOG_TAG, "IN POSTEXECUTE().....");
        }

        private URL CreateUrl(String Stringurl)
        {
            Log.e(LOG_TAG, "in CreateUrl()..... " );
            URL url;
            try
            {
                url = new URL(Stringurl);
            }
            catch (MalformedURLException e)
            {
                Log.e(LOG_TAG, "Error with Creating URL.", e);
                return null;
            }
            return url;
        }

        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";
            Log.e(LOG_TAG,"In MakeHHTPRequest().....");

            if (url == null) {
                return jsonResponse;
            }

            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.connect();

                if (urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromString(inputStream);
                } else {
                    Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem retrieving the book JSON results.", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        private String readFromString(InputStream inputStream) throws IOException
        {
            StringBuilder builder= new StringBuilder();
            if (inputStream !=null)
            {
                InputStreamReader inputStreamReader= new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line!=null)
                {
                    builder.append(line);
                    line=reader.readLine();
                }
            }
            return builder.toString();
        }

        private ArrayList<Book> extractFeatureFromJson(String bookJSON)
        {
            if (TextUtils.isEmpty(bookJSON))
            {
                return null;
            }
            booksList = new ArrayList<>();
            try
            {
                JSONObject baseJsonResponse = new JSONObject(bookJSON);
                JSONArray itemArray = baseJsonResponse.getJSONArray("item");

                ///ab hamare pas item ke bht sare object mojud hai to es ke liye hum forloop ka use krenge..
                if (itemArray.length() > 0){
                    for (int i=0; i<itemArray.length();i++)
                    {
                        JSONObject itemArrayJsonObject = itemArray.getJSONObject(i);
                        JSONObject volumnInfo = itemArrayJsonObject.getJSONObject("volumeInfo");
                        String title = volumnInfo.getString("title");

                        StringBuilder author = new StringBuilder();
                        if (volumnInfo.has("authors"))
                        {
                            JSONArray authorArray = volumnInfo.getJSONArray("authors");

                            for (int j=0; j<authorArray.length(); j++)
                            {
                                if (j > 0)
                                {
                                    author.append(", ");
                                }
                                author.append(authorArray.getString(j));
                            }

                            Book book = new Book(title, author.toString());

                            booksList.add(book);
                        }
                    }
                }
            }
             catch (JSONException e) {
                 Log.e(LOG_TAG, "Problem Parsing the Book JSON result,");
                e.printStackTrace();
             }
            return booksList;
        }

    }
}
