package com.example.asheransari.booklist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asher.ansari on 10/13/2016.
 */
public class BookAdapter extends ArrayAdapter<Book> {

    public BookAdapter(Context context, ArrayList<Book> books)
    {
        super(context,0, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View ListItemView = convertView;
        if (ListItemView == null)
        {
            ListItemView = LayoutInflater.from(getContext()).inflate(R.layout.book_list_item, parent,false);
        }
        Book currenBook = getItem(position);

        TextView title = (TextView)ListItemView.findViewById(R.id.bookTitleTextView);
        title.setText(currenBook.getmBookTitle());

        TextView author = (TextView)ListItemView.findViewById(R.id.bookAuthorTextView);
        author.setText(currenBook.getmBookAuthor());

        return ListItemView;
    }

}
