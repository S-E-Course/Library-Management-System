package com.library.model;

import java.time.LocalDate;

/**
 * Represents a borrowing record for a user and a media item.
 * Stores dates and the current status.
 */
public class Borrowing {
    private int borrowId;
    private int userId;
    private int mediaId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private String status;

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

    /** @return borrow date */
    public LocalDate getBorrowDate() { return borrowDate; }

    /** @param borrowDate borrow date */
    public void setBorrowDate(LocalDate borrowDate) { this.borrowDate = borrowDate; }

    /** @return due date */
    public LocalDate getDueDate() { return dueDate; }

    /** @param dueDate due date */
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    /** @return return date or null */
    public LocalDate getReturnDate() { return returnDate; }

    /** @param returnDate return date or null */
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    /** @return status string */
    public String getStatus() { return status; }

    /** @param status status string */
    public void setStatus(String status) { this.status = status; }

    /**
     * Checks if the item is overdue.
     *
     * @return true if overdue
     */
    public boolean isOverdue() {
        return returnDate == null && dueDate != null && dueDate.isBefore(LocalDate.now());
    }
}
