package com.library.strategy;

/**
 * Fine strategy for books.
 * The policy charges a flat rate of 10 NIS per overdue day.
 *
 * @author
 * @version 1.0
 */
public class BookFineStrategy implements FineStrategy {

    /**
     * Calculates the fine for an overdue book.
     *
     * @param overdueDays number of days the book is overdue
     * @return total fine amount in NIS (overdueDays * 10)
     */
    public int calculateFine(int overdueDays) {
        return overdueDays * 10;
    }
}
