package com.library.strategy;

/**
 * Fine strategy for CDs.
 * This strategy charges a rate of 20 NIS per overdue day.
 *
 * @author
 * @version 1.0
 */
public class CDFineStrategy implements FineStrategy {

    /**
     * Calculates the fine for an overdue CD.
     *
     * @param overdueDays number of days the CD is overdue
     * @return total fine amount in NIS (overdueDays * 20)
     */
    public int calculateFine(int overdueDays) {
        return overdueDays * 20;
    }
}
