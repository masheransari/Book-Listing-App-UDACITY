package com.example.asheransari.booklistinggoogle;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by asher.ansari on 10/10/2016.
 */
public class fetchBookTask extends AsyncTask<String,Void,ArrayList<Book>>{
    private final String LOG_TAG = fetchBookTask.class.getName();

    private BookAdapter mbookAdapter;

    public fetchBookTask(BookAdapter bookAdapter)
    {
        this.mbookAdapter = bookAdapter;
    }

    @Override
    protected ArrayList<Book> doInBackground(String... parms) {
        if (parms.length == 0) {
            return null;
        }

        HttpURLConnection urlConnection=null;
        BufferedReader reader =null;
        String rawJson =null;

        try
        {
            final String GOOGLE_BOOK_API = "https://www.googleapis.com/books/v1/volumes?";
            final String QUERY_PARAM = "q";

//            final Uri buildUri = Uri.parse(GOOGLE_BOOK_API).buildUpon().appendQueryParameter(QUERY_PARAM ,parms[0]).build();
//
//            final URL url = new URL(buildUri.toString());
            final Uri builtUri = Uri.parse(GOOGLE_BOOK_API).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, parms[0])
                    .build();

            final URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            final InputStream inputStream = urlConnection.getInputStream();

            final StringBuilder builder = new StringBuilder();

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while (  (line = reader.readLine()) !=null)
            {
                builder.append(line);
            }

            if (builder.length()==0)
            {
                return null;
            }

            rawJson = builder.toString();

        }
        catch(IOException e)
        {
            Log.e(LOG_TAG, "Error",e);
            return null;
        }

        finally {
            if (urlConnection !=null)
            {
                urlConnection.disconnect();
            }
            if (reader !=null)
            {
                try
                {
                    reader.close();
                }
                catch (final IOException e)
                {
                    Log.e(LOG_TAG, "Error Closing Stream",e);
                }
            }
        }

        return parseJson(rawJson);
    }


    private ArrayList<Book> parseJson(String rawJson)
    {
        final ArrayList<Book> books = new ArrayList<>();

        final String TITLE ="title";
        final String AUTHORs = "authors";
        final String ITEM_LIST = "items";
        final String VOLUMN_INFO = "volumnInfo";

        try
        {
            final JSONObject bookJson = new JSONObject(rawJson);
            final JSONArray itemArray = bookJson.getJSONArray(ITEM_LIST);

            ////see sample json link...
            ////https://www.googleapis.com/books/v1/volumes?q=harry+potter&callback=handleResponse
            ////http://www.jsoneditoronline.org/
            for (int i=0; i<itemArray.length(); i++)
            {
                final JSONObject item = itemArray.getJSONObject(i);

                final JSONObject bookVolumnInfo = item.getJSONObject(VOLUMN_INFO);
                ////phele hum book info se title ko set krden ge...

                final String title = bookVolumnInfo.getString(TITLE);

                final StringBuilder author = new StringBuilder();
                if (bookVolumnInfo.has(title))
                {
                    ///agar us ka title hai to us book ke authors ko fetch kro...
                    ////ab humein yeha yeh nhe maloom ke humare pas jo book a rhe hai us ka sirf ek he author hai... to es ke liye hum  for loop use kren ge..

                    ////phele hum bookinfo se author ke array me jaenge..
                    final JSONArray authorArray = bookVolumnInfo.getJSONArray(AUTHORs);
                    for(int j=0; j<authorArray.length(); j++)
                    {
                        author.append(authorArray.getString(j));

                        if (j<authorArray.length() -1)
                        {
                            author.append(",");
                        }
                    }
                }

                books.add(new Book(title, author.toString()));
            }

        }
        catch (JSONException e)
        {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    return books;
    }

    @Override
    protected void onPostExecute(ArrayList<Book> books)
    {
        if (books !=null && mbookAdapter!=null)
        {
            mbookAdapter.clear();

            for (Book book : books)
            {
                mbookAdapter.add(book);
            }
        }
    }

}
