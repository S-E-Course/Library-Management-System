package com.library.strategy;

/**
 * Fine strategy for journals.
 * Charges 15 NIS for each overdue day.
 */
public class JournalFineStrategy implements FineStrategy {

    /**
     * Calculates the fine for an overdue journal.
     *
     * @param overdueDays number of overdue days
     * @return fine amount in NIS
     */
    public int calculateFine(int overdueDays) {
        return overdueDays * 15;
    }
}
