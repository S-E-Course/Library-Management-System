package com.library.model;

/**
 * Concrete media type for compact discs.
 * Uses a borrow duration of seven days.
 *
 * @author
 * @version 1.0
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
