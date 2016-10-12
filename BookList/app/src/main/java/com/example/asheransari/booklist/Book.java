package com.example.asheransari.booklist;

/**
 * Created by asher.ansari on 10/13/2016.
 */
public class Book {
    private String mBookTitle;
    private String mBookAuthor;

    public Book(String title, String author)
    {
        mBookAuthor = author;
        mBookTitle = title;
    }

    public String getmBookAuthor() {
        return mBookAuthor;
    }

    public String getmBookTitle() {
        return mBookTitle;
    }
}
