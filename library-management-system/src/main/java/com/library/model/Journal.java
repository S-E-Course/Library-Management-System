package com.library.model;

/**
 * Media type for journals.
 * Has a borrow period of fourteen days.
 */
public class Journal extends Media {

    /** @return media type string */
    @Override
    public String getType() {
        return "journal";
    }

    /** @return borrow duration in days */
    @Override
    public int getBorrowDurationDays() {
        return 14;
    }
}
