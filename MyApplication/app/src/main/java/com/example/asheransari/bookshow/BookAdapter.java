package com.example.asheransari.bookshow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import com.squareup.picasso.Picasso;

/**
 * Created by asher.ansari on 10/13/2016.
 */
public class BookAdapter extends ArrayAdapter<Book> {

    // context is the current context (i.e. Activity) that the adapter is being created in
    // detail is the list of detail to be displayed.
    // colorResourceId is the resource ID for the background color for this list of detail
    public BookAdapter(Context context, ArrayList<Book> books) {
        super(context, 0, books);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // check if there is an existing list item view (called convertView) that we can reuse,
        //otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.activity_list, parent, false);

        }

        //find the book at the give position in the list of books
        Book currentBook = getItem(position);

        //Find the ImageView with view ID
        ImageView bookImage = (ImageView) listItemView.findViewById(R.id.book_picture);
        //display the book picture of the current book in the ImageView
        Picasso.with(getContext()).load(currentBook.getmPicture()).into(bookImage);

        TextView bookTitle = (TextView) listItemView.findViewById(R.id.book_title);
        bookTitle.setText(currentBook.getmTitle());

        TextView bookAuthor = (TextView) listItemView.findViewById(R.id.book_author);
        bookAuthor.setText(currentBook.getmAuthor());

        TextView bookPublisher = (TextView) listItemView.findViewById(R.id.book_publisher);
        bookPublisher.setText(currentBook.getmPublisher());

        TextView bookCategory = (TextView) listItemView.findViewById(R.id.book_category);
        bookCategory.setText(currentBook.getmCategory());

        RatingBar bookRating = (RatingBar) listItemView.findViewById(R.id.book_rating);
        bookRating.setRating((float) currentBook.getmRating());


        //Return the list item view that is now showing the appropriate date
        return listItemView;

    }

}