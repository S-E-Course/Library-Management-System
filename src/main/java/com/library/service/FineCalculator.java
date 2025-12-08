package com.library.service;

import com.library.strategy.*;

/**
 * Calculates fines using a selected fine strategy.
 * The strategy decides how the fine is computed.
 */
public class FineCalculator {

    private FineStrategy fineStrategy;

    /**
     * Creates a calculator with an initial strategy.
     *
     * @param fineStrategy strategy used for fine calculation
     */
    public FineCalculator(FineStrategy fineStrategy) {
        this.fineStrategy = fineStrategy;
    }

    /**
     * Computes the fine based on the number of overdue days.
     *
     * @param overdueDays number of days overdue
     * @return fine amount
     */
    public int calculateFine(int overdueDays) {
        return fineStrategy.calculateFine(overdueDays);
    }

    /**
     * Changes the strategy used for fine calculation.
     *
     * @param fineStrategy new strategy
     */
    public void setFineStrategy(FineStrategy fineStrategy) {
        this.fineStrategy = fineStrategy;
    }
}
