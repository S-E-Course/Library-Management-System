package com.library.model;

import java.time.LocalDate;

/**
 * Borrowing record linking a user and a media item.
 * Tracks dates and status values such as borrowed, overdue, returned.
 *
 * @author
 * @version 1.0
 */
public class Borrowing {
    private int borrowId;
    private int userId;
    private int mediaId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;  // may be null
    private String status;         // borrowed, overdue, returned

    /** @return borrowing identifier */
    public int getBorrowId() { return borrowId; }

    /** @param borrowId borrowing identifier */
    public void setBorrowId(int borrowId) { this.borrowId = borrowId; }

    /** @return user identifier */
    public int getUserId() { return userId; }

    /** @param userId user identifier */
    public void setUserId(int userId) { this.userId = userId; }

    /** @return media identifier */
    public int getMediaId() { return mediaId; }

    /** @param mediaId media identifier */
    public void setMediaId(int mediaId) { this.mediaId = mediaId; }

    /** @return date the item was borrowed */
    public LocalDate getBorrowDate() { return borrowDate; }

    /** @param borrowDate date the item was borrowed */
    public void setBorrowDate(LocalDate borrowDate) { this.borrowDate = borrowDate; }

    /** @return due date */
    public LocalDate getDueDate() { return dueDate; }

    /** @param dueDate due date */
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    /** @return date returned or null */
    public LocalDate getReturnDate() { return returnDate; }

    /** @param returnDate date returned or null */
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    /** @return status string such as borrowed, overdue, returned */
    public String getStatus() { return status; }

    /** @param status status string */
    public void setStatus(String status) { this.status = status; }

    /**
     * Returns true if the item is not yet returned and the due date is before today.
     *
     * @return true when overdue
     */
    public boolean isOverdue() {
        return returnDate == null && dueDate != null && dueDate.isBefore(LocalDate.now());
    }
}
