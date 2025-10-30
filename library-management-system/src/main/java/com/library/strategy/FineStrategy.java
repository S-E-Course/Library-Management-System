package com.library.strategy;

public interface FineStrategy {
    /**
     * Calculates fine amount.
     * @param overdueDays number of days overdue
     * @return total fine in NIS
     */
    int calculateFine(int overdueDays);
}
