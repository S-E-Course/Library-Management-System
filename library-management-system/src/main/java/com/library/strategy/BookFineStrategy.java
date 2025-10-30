package com.library.strategy;

public class BookFineStrategy implements FineStrategy {
	
    public int calculateFine(int overdueDays) {
        return overdueDays * 10;
    }
}