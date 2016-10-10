package com.example.asheransari.booklistinggoogle;

/**
 * Created by asher.ansari on 10/10/2016.
 */
public class Book {

    private String title;
    private String author;
    public Book(String tit, String aut)
    {
        title = tit;
        author = aut;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }
}
