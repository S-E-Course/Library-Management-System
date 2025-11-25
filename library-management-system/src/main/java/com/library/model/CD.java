package com.library.model;

/**
 * Media type representing a cd.
 * Has a borrow duration of seven days.
 */
public class CD extends Media {

    /** {@inheritDoc} */
    @Override
    public String getType() {
        return "cd";
    }

    /** {@inheritDoc} */
    @Override
    public int getBorrowDurationDays() {
        return 7;
    }
}
