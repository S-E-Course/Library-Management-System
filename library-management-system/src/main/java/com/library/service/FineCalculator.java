package com.library.service;

import com.library.strategy.*;

/**
 * Strategy-based fine calculator.
 * Delegates fine calculation to a provided {@link FineStrategy} implementation.
 */
public class FineCalculator {

    private FineStrategy fineStrategy;

    /**
     * Creates a calculator with the given fine strategy.
     *
     * @param fineStrategy strategy to use (e.g., book, cd, journal)
     */
    public FineCalculator(FineStrategy fineStrategy) {
        this.fineStrategy = fineStrategy;
    }

    /**
     * Calculates a fine for the given number of overdue days using the current strategy.
     *
     * @param overdueDays number of days overdue
     * @return fine amount calculated by the strategy
     */
    public int calculateFine(int overdueDays) {
        return fineStrategy.calculateFine(overdueDays);
    }

    /**
     * Replaces the current fine strategy.
     *
     * @param fineStrategy new strategy to apply
     */
    public void setFineStrategy(FineStrategy fineStrategy) {
        this.fineStrategy = fineStrategy;
    }
}
