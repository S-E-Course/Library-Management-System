package com.library.model;

/**
 * Concrete media type for books.
 * Uses a borrow duration of twenty eight days.
 *
 * @author
 * @version 1.0
 */
public class Book extends Media {

    /** {@inheritDoc} */
    @Override
    public String getType() {
        return "book";
    }

    /** {@inheritDoc} */
    @Override
    public int getBorrowDurationDays() {
        return 28;
    }
}
