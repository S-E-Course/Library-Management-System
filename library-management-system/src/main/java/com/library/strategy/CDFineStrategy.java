package com.library.strategy;

public class CDFineStrategy implements FineStrategy {
	
    public int calculateFine(int overdueDays) {
        return overdueDays * 20;
    }
}