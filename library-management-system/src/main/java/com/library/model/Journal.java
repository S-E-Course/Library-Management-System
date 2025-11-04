package com.library.model;

/**
 * Concrete media type for journals.
 * Uses a borrow duration of fourteen days.
 *
 * @author
 * @version 1.0
 */
public class Journal extends Media {

    /** {@inheritDoc} */
    @Override
    public String getType() {
        return "journal";
    }

    /** {@inheritDoc} */
    @Override
    public int getBorrowDurationDays() {
        return 14;
    }
}
