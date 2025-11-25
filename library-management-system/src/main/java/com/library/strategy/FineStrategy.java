package com.library.strategy;

/**
 * Strategy interface for calculating fines.
 */
public interface FineStrategy {

    /**
     * Calculates the fine based on overdue days.
     *
     * @param overdueDays number of overdue days
     * @return fine amount in NIS
     */
    int calculateFine(int overdueDays);
}
