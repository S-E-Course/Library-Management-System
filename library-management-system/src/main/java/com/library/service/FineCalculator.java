package com.library.service;

import com.library.strategy.*;

public class FineCalculator {
	
    private FineStrategy fineStrategy;

    public FineCalculator(FineStrategy fineStrategy) {
        this.fineStrategy = fineStrategy;
    }

    public int calculateFine(int overdueDays) {
        return fineStrategy.calculateFine(overdueDays);
    }

    public void setFineStrategy(FineStrategy fineStrategy) {
        this.fineStrategy = fineStrategy;
    }
}
