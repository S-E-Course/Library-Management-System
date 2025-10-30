package com.library.model;

public class Book extends Media {
    
    public int getBorrowDurationDays() { return 28; }

    
    public String getType() { return "book"; }
}