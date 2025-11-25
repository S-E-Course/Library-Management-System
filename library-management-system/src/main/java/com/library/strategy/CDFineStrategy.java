package com.library.strategy;

/**
 * Fine strategy for CDs.
 * Charges 20 NIS for each overdue day.
 */
public class CDFineStrategy implements FineStrategy {

    /**
     * Returns the fine amount for an overdue CD.
     *
     * @param overdueDays number of overdue days
     * @return fine amount in NIS
     */
    public int calculateFine(int overdueDays) {
        return overdueDays * 20;
    }
}
