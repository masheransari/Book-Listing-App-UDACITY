package com.example.asheransari.bookshow;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
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
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getName();

    public String userBookSearch;

    private EditText userInput;

//    private Button searchButton;

    private ArrayList<Book> tempBookArrayList = null;

//    static class ViewHolderItem
//    {
//        ImageView bookImage;
//        TextView bookTitle;
//        TextView bookAuthor;
//        TextView bookPublisher;
//        TextView bookCatagory;
//        RatingBar bookRating;
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView bookListView = (ListView)findViewById(R.id.list);

        userInput = (EditText)findViewById(R.id.editText);
        Button searchButton;
        searchButton = (Button)findViewById(R.id.bookSearchBtn);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //storing the text in a string called userBookSearch/ .replaceAll,
                //added to be able to search for multiple words
                userBookSearch = userInput.getText().toString().replaceAll(" ", "+");
                //Logging the search term the user entered
                Log.v(LOG_TAG, userBookSearch);
                //if user doesn't enter a search term a toast will show,
                //and then it will be logged
                if (userBookSearch.trim().length() <= 0 || userBookSearch.length() <= 0) {
                    Toast.makeText(getApplicationContext(), "No Search Entered", Toast.LENGTH_LONG).show();
                    Log.e(LOG_TAG, "Error Response code: No Search Given");
                    //if a search term is entered continue with task and log search term
                } else {
                    Log.v(LOG_TAG, userBookSearch);
                    BookAsyncTask task = new BookAsyncTask();
                    task.execute();
                }
            }
        });
                if (tempBookArrayList != null) {
                    BookAdapter adapter = new BookAdapter(this, tempBookArrayList);
                    bookListView.setAdapter(adapter);
                }
            }

            private void updateUi(ArrayList<Book> books) {
                tempBookArrayList = books;

                if (books != null) {
                    ListView bookListView = (ListView) findViewById(R.id.list);

                    BookAdapter adapter = new BookAdapter(this, books);

                    bookListView.setAdapter(adapter);
                } else {
                    Log.e(LOG_TAG, "Still suffering from random void errors and no results with a correct string");
                }
            }

            private class BookAsyncTask extends AsyncTask<URL, Void, ArrayList<Book>> {

                @Override
                protected ArrayList<Book> doInBackground(URL... urls) {

                    URL url;
                    url = createUrl(userBookSearch.trim());
                    Log.e(LOG_TAG, "in DoInBackground OverdideMethod..." + url);
                    String jsonResponce = "";
                    try {
                        jsonResponce = makeHttpRequest(url);
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "" + e);
                    }

                    ArrayList<Book> books = extractBookFromJson(jsonResponce);

                    userBookSearch = "";
                    return books;
                }

                @Override
                protected void onPostExecute(ArrayList<Book> books) {
//                super.onPostExecute(books);
                    if (books == null) {
                        return;
                    }
                    updateUi(books);
                }

                private URL createUrl(String searchItem1) {
                    String baseUrl = "https://www.googleapis.com/books/v1/volumes?q=";
                    String completeURL = baseUrl + searchItem1.replace(" ", "20%");
                    URL url = null;
                    try {
                        url = new URL(completeURL);
                    } catch (MalformedURLException e) {
                        Log.e(LOG_TAG, "Error Creating URl", e);
                    }
                    Log.e(LOG_TAG, "Compelelte URL = " + completeURL);
                    return url;
                }

                private String makeHttpRequest(URL url) throws IOException {
                    String jsonResponce = "";
                    if (url == null) {
                        return jsonResponce;
                    }

                    HttpURLConnection urlConnection = null;
                    InputStream inputStream = null;

                    try {
                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestMethod("GET");
                        urlConnection.setReadTimeout(10000);
                        urlConnection.setConnectTimeout(15000);
                        urlConnection.connect();

                        if (urlConnection.getResponseCode() == 200) {
                            Log.e(LOG_TAG, "Responce Code =" + urlConnection.getResponseCode());
                            inputStream = urlConnection.getInputStream();
                            jsonResponce = readFromStream(inputStream);
                        } else {
                            Log.e(LOG_TAG, "Eror Responce Code =" + urlConnection.getResponseCode() + " " + url.toString());
                        }
                    } catch (IOException e) {
                        assert urlConnection != null;
                        if (urlConnection.getResponseCode() != 200) {
                            Log.e(LOG_TAG, "In MakeHttpRequest Method", e);
                        }
                    } finally {
                        if (urlConnection != null) {
                            urlConnection.disconnect();
                        }
                        if (inputStream != null) {
                            inputStream.close();
                        }
                    }

                    return jsonResponce;
                }

                private String readFromStream(InputStream inputStream) throws IOException {
                    StringBuilder builder = new StringBuilder();
                    if (inputStream != null) {
                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                        BufferedReader reader = new BufferedReader(inputStreamReader);
                        String line = reader.readLine();

                        while (line != null) {
                            builder.append(line);
                            line = reader.readLine();
                        }
                    }
                    return builder.toString();

                }


                private ArrayList<Book> extractBookFromJson(String bookJson) {
                    if (TextUtils.isEmpty(bookJson)) {
                        return null;
                    }
                    ArrayList<Book> books = new ArrayList<>();

//                    try {
//                        JSONObject baseJsonObject = new JSONObject(bookJson);
//                        JSONArray bookArray = baseJsonObject.getJSONArray("items");
//                        int length = bookArray.length();
//
//                        for (int i = 0; i < length; i++) {
//                            String author = "Author: ";
//                            String category = "";
//                            String publisher = "Publisher: ";
//                            double rating = 0.0;
//
//                            JSONObject bookObject = bookArray.getJSONObject(i);
//                            JSONObject bookInfo = bookObject.getJSONObject("volumeInfo");
//                            JSONObject bookPicture = bookInfo.getJSONObject("imageLinks");
//
//                            String picture = bookPicture.getString("thumbnail");
//
//                            String title = bookInfo.getString("title");
//                            publisher += bookInfo.getString("publisher");
//
//                            if (bookInfo.isNull("averageRating")) {
//                                rating = 5;
//                            } else {
//                                rating = bookInfo.getDouble("averageRating");
//                            }
//
//                            JSONArray authors = bookInfo.getJSONArray("authors");
//
//                            if (authors.length() > 0) {
//                                for (int j = 0; j < authors.length(); j++) {
//                                    author += authors.optString(j) + " ";
//                                }
//                            }
//
//                            JSONArray categories = bookInfo.getJSONArray("categories");
//
//                            if (categories.length() > 0) {
//                                for (int j = 0; j < categories.length(); j++) {
//                                    category += categories.optString(j) + " ";
//                                }
//                            }
//                            Log.v(LOG_TAG, title + "" + author + "" + publisher + "" + rating + "" + category + "" + picture);
//                            books.add(new Book(rating, title, author, publisher, category, picture));
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                    return books;
                    try {
                        JSONObject baseJsonResponse = new JSONObject(bookJson);
                        JSONArray bookArray = baseJsonResponse.getJSONArray("items");
                        int length = bookArray.length();
                        for (int i = 0; i < length; i++) {

                            /**
                             * Temporary variables for storing/augmenting data to push to a book object
                             */
                            String author = "Author: ";
                            String category = "";
                            String publisher = "Publisher: ";
                            double rating;

                            JSONObject bookObject = bookArray.getJSONObject(i);
                            JSONObject bookInfo = bookObject.getJSONObject("volumeInfo");
                            JSONObject bookPictures = bookInfo.getJSONObject("imageLinks");

                            String picture = bookPictures.getString("thumbnail");


                            String title = bookInfo.getString("title");
                            publisher += bookInfo.getString("publisher");
                            if (bookInfo.isNull("averageRating")) {
                                // Default unrated value? Hmm...
                                rating = 5;
                            } else {
                                rating = bookInfo.getDouble("averageRating");
                            }

                            JSONArray authors = bookInfo.getJSONArray("authors");

                            /**
                             * Loop functions for the author(s) array
                             */
                            if (authors.length() > 0) {
                                for (int j = 0; j < authors.length(); j++) {
                                    author += authors.optString(j) + " ";
                                }
                            }

                            /**
                             * Loop functions for the category(ies) array
                             */
                            JSONArray categories = bookInfo.getJSONArray("categories");

                            if (categories.length() > 0) {
                                for (int j = 0; j < categories.length(); j++) {
                                    category += categories.optString(j) + " ";
                                }
                            }

                            Log.v(LOG_TAG, title + " " + author + " " + publisher + " " + rating + " " +
                                    category + " " + picture);
                            books.add(new Book(rating, title, author, publisher, category, picture));

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return books;
                }

            }


        }

