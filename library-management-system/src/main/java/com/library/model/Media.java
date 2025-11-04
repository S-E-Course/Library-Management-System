package com.library.model;

/**
 * Base class for library media.
 * Subclasses represent concrete types such as Book, CD, and Journal.
 * A media item is available when it can be borrowed.
 *
 * @author
 * @version 1.0
 */
public abstract class Media {
    private int id;
    private String title;
    private String author;
    private String isbn;
    private boolean available = true;

    /**
     * Returns a lowercase type name such as book, cd, or journal.
     *
     * @return media type name
     */
    public abstract String getType();

    /**
     * Returns the default borrow duration in days for this media type.
     *
     * @return number of days the item may be borrowed
     */
    public abstract int getBorrowDurationDays();

    /** @return database identifier */
    public int getId() { return id; }

    /** @param id database identifier */
    public void setId(int id) { this.id = id; }

    /** @return title */
    public String getTitle() { return title; }

    /** @param title title */
    public void setTitle(String title) { this.title = title; }

    /** @return author */
    public String getAuthor() { return author; }

    /** @param author author */
    public void setAuthor(String author) { this.author = author; }

    /** @return ISBN string */
    public String getIsbn() { return isbn; }

    /** @param isbn ISBN string */
    public void setIsbn(String isbn) { this.isbn = isbn; }

    /** @return true if available to borrow */
    public boolean isAvailable() { return available; }

    /** @param available availability flag */
    public void setAvailable(boolean available) { this.available = available; }
}
