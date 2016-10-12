package com.example.asheransari.booklist;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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
    ArrayList<Book> booksList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e(LOG_TAG, "in OnCreate()...");

        final EditText inputString = (EditText) findViewById(R.id.edit_text_view);

        Button searchButton = (Button)findViewById(R.id.searchBtn);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String search = inputString.getText().toString().replace(" ","+");
                String url = (BOOK_REQUEST_URL + search);
                if (isOnline(getApplicationContext()))
                {
                    BookAsyncTask task = new BookAsyncTask();
                    task.execute(url);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "No Internet Connection\n Please Retry Again",Toast.LENGTH_LONG).show();
                }
            }
        });

        ListView bookListView = (ListView) findViewById(R.id.list);
        bookListView.setEmptyView(findViewById(R.id.empty_list_view));
        mBookAdapter = new BookAdapter(this, booksList);
        bookListView.setAdapter(mBookAdapter);
    }

    public boolean isOnline(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        boolean isConnected = info !=null && info.isConnectedOrConnecting();
        Log.e(LOG_TAG, "interent Status = "+isConnected);
        return isConnected;
    }

    private class BookAsyncTask extends AsyncTask<String,Void, ArrayList<Book>>
    {

        @Override
        protected ArrayList<Book> doInBackground(String... urls) {

            URL url = createUrl(urls[0]);

            String jsonResponce = "";
            try
            {
                jsonResponce = makeHttpRequest(url);
            }
            catch(IOException e)
            {
                Log.e(LOG_TAG, "Problem in Making HTTP request.",e);
            }
            ArrayList<Book> books = extractFeaturesFromJson(jsonResponce);

            return books;
        }

        @Override
        protected void onPostExecute(ArrayList<Book> books) {
            mBookAdapter.clear();
            if (books != null)
            {
                mBookAdapter.addAll(books);
            }
        }

        private URL createUrl(String StringUrl)
        {
            URL url = null;
            try
            {
                url = new URL(StringUrl);
            }
            catch (MalformedURLException e)
            {
                Log.e(LOG_TAG, "Error with Creating URL", e);
            }
            return url;
        }

        private String makeHttpRequest(URL url) throws IOException
        {
            String jsonResponse = null;

            if (url == null)
            {
                return jsonResponse;
            }

            HttpURLConnection urlConnection=null;
            InputStream inputStream=null;

            try
            {
                urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.connect();

                if (urlConnection.getResponseCode() == 200)
                {
                    inputStream= urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                }
                else
                {
                    Log.e(LOG_TAG, "Error Response Code :"+urlConnection.getResponseCode());
                }
            }
            catch (IOException e)
            {
                Log.e(LOG_TAG, "Problem Retrieving the Book JSON Result.",e);
            }
            finally {
                if (urlConnection != null)
                {
                    urlConnection.disconnect();
                }
                if (inputStream!=null)
                {
                    inputStream.close();
                }
            }

            return jsonResponse;
        }
private String readFromStream(InputStream inputStream)throws IOException
{
    StringBuilder output = new StringBuilder();
    if (inputStream != null)
    {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream , Charset.forName("UTF-8"));
        BufferedReader reader = new BufferedReader(inputStreamReader);
        String line = reader.readLine();
        while (line!=null)
        {
            output.append(line);
            line = reader.readLine();
        }
    }
    return output.toString();
}

        private ArrayList<Book> extractFeaturesFromJson(String BookJson)
        {
            if (TextUtils.isEmpty(BookJson))
            {
                return null;
            }

            booksList = new ArrayList<>();

            try {
                JSONObject baseJsonResponse = new JSONObject(BookJson);
                JSONArray itemsArray = baseJsonResponse.getJSONArray("items");
                if (itemsArray.length() > 0) {
                    for (int i = 0; i < itemsArray.length(); i++) {
                        JSONObject itemsArrayJSONObject = itemsArray.getJSONObject(i);
                        JSONObject volumeInfo = itemsArrayJSONObject.getJSONObject("volumeInfo");
                        String title = volumeInfo.getString("title");

                        StringBuilder authors = new StringBuilder();
                        if (volumeInfo.has("authors")) {
                            JSONArray authorsArray = volumeInfo.getJSONArray("authors");

                            for (int j = 0; j < authorsArray.length(); j++) {
                                if(j >0){
                                    authors.append(", ");
                                }
                                authors.append(authorsArray.getString(j));
                            }
                            Book book = new Book(title, authors.toString());
                            booksList.add(book);
                        }
                    }
                }

            } catch (JSONException e) {
                Log.e(LOG_TAG, "Problem parsing the book JSON results", e);
            }
            return booksList;
        }
    }
}


//    private  Context mContext;
//    private BookAdapter mBookAdapter;
//    public static final String LOG_TAG = MainActivity.class.getSimpleName();
//    private static final String BOOK_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?q=";
//    ArrayList<Book> booksList;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Log.v(LOG_TAG, "in oncreate()");
//        setContentView(R.layout.activity_main);
//
//
//        final EditText inputString = (EditText) findViewById(R.id.edit_text_view);
//
//        Button searchButton = (Button) findViewById(R.id.searchBtn);
//        searchButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String search = inputString.getText().toString().replace(" ", "+");
//                String url = (BOOK_REQUEST_URL + search);
//                if (isOnline(getApplicationContext())) {
//                    BookAsyncTask task = new BookAsyncTask();
//                    task.execute(url);
//                } else {
//                    Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//        ListView bookListView = (ListView) findViewById(R.id.list);
//        bookListView.setEmptyView(findViewById(R.id.empty_list_view));
//        mBookAdapter = new BookAdapter(this, booksList);
//        bookListView.setAdapter(mBookAdapter);
//    }
//
//
//    public boolean isOnline(Context context) {
//        ConnectivityManager cm =
//                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//        boolean isConnected = activeNetwork != null &&
//                activeNetwork.isConnectedOrConnecting();
//        return isConnected;
//    }
//
//    private class BookAsyncTask extends AsyncTask<String, Void, ArrayList<Book>>{
//
//        @Override
//        protected ArrayList<Book> doInBackground(String... urls) {
//
//            //example url = https://www.googleapis.com/books/v1/volumes?q=Gladwell
//            URL url = createUrl(urls[0]);
//
//            String jsonResponse = "";
//            try {
//                jsonResponse = makeHttpRequest(url);
//            } catch (IOException e) {
//                Log.e(LOG_TAG, "Problem making the HTTP request.", e);
//            }
//            ArrayList<Book> books = extractFeatureFromJson(jsonResponse);
//            return books;
//        }
//
//        @Override
//        protected void onPostExecute(ArrayList<Book> books) {
//            mBookAdapter.clear();
//
//            if (books != null) {
//                mBookAdapter.addAll(books);
//            }
//        }
//
//        private URL createUrl(String stringUrl) {
//            URL url;
//            try {
//                url = new URL(stringUrl);
//            } catch (MalformedURLException exception) {
//                Log.e(LOG_TAG, "Error with creating URL", exception);
//                return null;
//            }
//            return url;
//        }
//
//        private String makeHttpRequest(URL url) throws IOException {
//            String jsonResponse = "";
//
//            if (url == null) {
//                return jsonResponse;
//            }
//
//            HttpURLConnection urlConnection = null;
//            InputStream inputStream = null;
//            try {
//                urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setRequestMethod("GET");
//                urlConnection.setReadTimeout(10000 /* milliseconds */);
//                urlConnection.setConnectTimeout(15000 /* milliseconds */);
//                urlConnection.connect();
//
//                if (urlConnection.getResponseCode() == 200) {
//                    inputStream = urlConnection.getInputStream();
//                    jsonResponse = readFromStream(inputStream);
//                } else {
//                    Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
//                }
//            } catch (IOException e) {
//                Log.e(LOG_TAG, "Problem retrieving the book JSON results.", e);
//            } finally {
//                if (urlConnection != null) {
//                    urlConnection.disconnect();
//                }
//                if (inputStream != null) {
//                    inputStream.close();
//                }
//            }
//            return jsonResponse;
//        }
//
//        private String readFromStream(InputStream inputStream) throws IOException {
//            StringBuilder output = new StringBuilder();
//            if (inputStream != null) {
//                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
//                BufferedReader reader = new BufferedReader(inputStreamReader);
//                String line = reader.readLine();
//                while (line != null) {
//                    output.append(line);
//                    line = reader.readLine();
//                }
//            }
//            return output.toString();
//        }
//
//        private ArrayList<Book> extractFeatureFromJson(String bookJSON) {
//
//            if (TextUtils.isEmpty(bookJSON)) {
//                return null;
//            }
//
//            booksList = new ArrayList<>();
//
//            try {
//                JSONObject baseJsonResponse = new JSONObject(bookJSON);
//                JSONArray itemsArray = baseJsonResponse.getJSONArray("items");
//                if (itemsArray.length() > 0) {
//                    for (int i = 0; i < itemsArray.length(); i++) {
//                        JSONObject itemsArrayJSONObject = itemsArray.getJSONObject(i);
//                        JSONObject volumeInfo = itemsArrayJSONObject.getJSONObject("volumeInfo");
//                        String title = volumeInfo.getString("title");
//
//                        StringBuilder authors = new StringBuilder();
//                        if (volumeInfo.has("authors")) {
//                            JSONArray authorsArray = volumeInfo.getJSONArray("authors");
//
//                            for (int j = 0; j < authorsArray.length(); j++) {
//                                if(j >0){
//                                    authors.append(", ");
//                                }
//                                authors.append(authorsArray.getString(j));
//                            }
//                            Book book = new Book(title, authors.toString());
//                            booksList.add(book);
//                        }
//                    }
//                }
//
//            } catch (JSONException e) {
//                Log.e(LOG_TAG, "Problem parsing the book JSON results", e);
//            }
//            return booksList;
//        }
//    }
//}
