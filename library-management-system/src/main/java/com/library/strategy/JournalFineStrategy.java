package com.library.strategy;

/**
 * Fine strategy for journals.
 * This strategy applies a rate of 15 NIS per overdue day.
 *
 * @author
 * @version 1.0
 */
public class JournalFineStrategy implements FineStrategy {

    /**
     * Calculates the fine for an overdue journal.
     *
     * @param overdueDays number of days the journal is overdue
     * @return total fine amount in NIS (overdueDays * 15)
     */
    public int calculateFine(int overdueDays) {
        return overdueDays * 15;
    }
}
