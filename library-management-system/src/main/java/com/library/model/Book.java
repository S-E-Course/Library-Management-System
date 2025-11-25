package com.library.model;

/**
 * Media type representing a book.
 * A book can be borrowed for 28 days.
 */
public class Book extends Media {

    @Override
    public String getType() {
        return "book";
    }

    @Override
    public int getBorrowDurationDays() {
        return 28;
    }
}
