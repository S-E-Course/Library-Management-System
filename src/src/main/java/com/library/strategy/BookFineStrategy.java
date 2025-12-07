package com.library.strategy;

/**
 * Fine strategy for books.
 * Charges 10 NIS for each overdue day.
 */
public class BookFineStrategy implements FineStrategy {

    /**
     * Returns the fine amount for an overdue book.
     *
     * @param overdueDays number of overdue days
     * @return fine amount in NIS
     */
    public int calculateFine(int overdueDays) {
        return overdueDays * 10;
    }
}
