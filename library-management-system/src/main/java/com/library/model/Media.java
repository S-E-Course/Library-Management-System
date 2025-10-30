package com.library.model;

public abstract class Media {
    private int id;
    private String title;
    private String author;
    private String isbn;
    private boolean available;

    public int getId() {
    	return id;
    }
    public void setId(int id) {
    	this.id = id;
    }

    public String getTitle() {
    	return title;
    }
    public void setTitle(String title) {
    	this.title = title;
    }

    public String getAuthor() {
    	return author;
    }
    public void setAuthor(String author) {
    	this.author = author;
    }

    public String getIsbn() {
    	return isbn;
    }
    public void setIsbn(String isbn) {
    	this.isbn = isbn;
    }

    public boolean isAvailable() {
    	return available;
    }
    public void setAvailable(boolean available) {
    	this.available = available;
    }

    /** Number of days this media can be borrowed */
    public abstract int getBorrowDurationDays();

    public abstract String getType();
}
