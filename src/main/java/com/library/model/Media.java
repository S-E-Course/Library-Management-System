package com.library.model;

/**
 * Base class for media items.
 * Subclasses provide specific types such as book, cd, or journal.
 */
public abstract class Media {
    private int id;
    private String title;
    private String author;
    private String isbn;
    private boolean available = true;

    /**
     * Returns the media type name in lowercase.
     *
     * @return type name
     */
    public abstract String getType();

    /**
     * Returns the borrow duration in days.
     *
     * @return number of days allowed
     */
    public abstract int getBorrowDurationDays();

    /** @return item identifier */
    public int getId() { return id; }

    /** @param id item identifier */
    public void setId(int id) { this.id = id; }

    /** @return title text */
    public String getTitle() { return title; }

    /** @param title title text */
    public void setTitle(String title) { this.title = title; }

    /** @return author name */
    public String getAuthor() { return author; }

    /** @param author author name */
    public void setAuthor(String author) { this.author = author; }

    /** @return ISBN value */
    public String getIsbn() { return isbn; }

    /** @param isbn ISBN value */
    public void setIsbn(String isbn) { this.isbn = isbn; }

    /** @return true if available to borrow */
    public boolean isAvailable() { return available; }

    /** @param available availability flag */
    public void setAvailable(boolean available) { this.available = available; }
}
