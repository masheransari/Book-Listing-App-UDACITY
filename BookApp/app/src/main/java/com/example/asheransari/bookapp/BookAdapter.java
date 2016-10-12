package com.example.asheransari.bookapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by asher.ansari on 10/12/2016.
 */
public class BookAdapter extends ArrayAdapter<Book>{

    public BookAdapter(Context c , ArrayList<Book> books)
    {
        super(c,0,books);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View listItemView = convertView;
        if (listItemView == null)
        {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.book_list_item, parent,false);
        }

        Book currentBook = getItem(position);

        TextView title = (TextView) listItemView.findViewById(R.id.bookTitleTextView);
        title.setText(currentBook.getmBookTitle());

        TextView author= (TextView) listItemView.findViewById(R.id.bookAuthorTextView);
        author.setText(currentBook.getmBookAuthor());

        return listItemView;
    }
}
