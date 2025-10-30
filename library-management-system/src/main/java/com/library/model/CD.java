package com.library.model;

public class CD extends Media {
    public int getBorrowDurationDays() { return 7; }

    public String getType() { return "cd"; }
}