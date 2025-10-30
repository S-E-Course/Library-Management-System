package com.library.strategy;

public class JournalFineStrategy implements FineStrategy {
	
    public int calculateFine(int overdueDays) {
        return overdueDays * 15;
    }
}