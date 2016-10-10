package com.example.asheransari.booklistinggoogle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by asher.ansari on 10/10/2016.
 */
public class BookAdapter extends ArrayAdapter<Book>{

    public BookAdapter(Context context, ArrayList<Book> books)
    {
        super(context,0, books);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final Book book = getItem(position);
        final ViewHolder viewHolder;
        ///yeh hamare pas ek class bne hui hai.. ur hum ne uska ek object create kia hai..
        ///yeh class neche hai bne hui hai,,,

        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_book,parent,false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        final String author;
        if (book.getAuthor().isEmpty())
        {
            author= getContext().getString(R.string.unknown_author);
        }
        else
        {
            author = getContext().getString(R.string.authors, book.getAuthor());
        }
        viewHolder.bookAuthorView.setText(author);
        viewHolder.bookTitleView.setText(book.getTitle());

        return convertView;
    }

    public static final class ViewHolder {
        private final TextView bookTitleView;
        private final TextView bookAuthorView;


        public ViewHolder(View view) {

            bookTitleView =(TextView)view.findViewById(R.id.text_Book_title);
            bookAuthorView =(TextView)view.findViewById(R.id.text_book_author);
        }
    }
}
