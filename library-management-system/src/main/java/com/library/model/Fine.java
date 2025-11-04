package com.library.model;

import java.time.LocalDate;

/**
 * Fine assessed for a borrowing.
 * Stores amount, payment status, and the date the fine was created or updated.
 *
 * @author
 * @version 1.0
 */
public class Fine {
    private int id;
    private int userId;
    private int borrowId;
    private double amount;
    private boolean paid;
    private LocalDate fineDate;

    /** @return fine identifier */
    public int getId() { return id; }

    /** @param id fine identifier */
    public void setId(int id) { this.id = id; }

    /** @return user identifier */
    public int getUserId() { return userId; }

    /** @param userId user identifier */
    public void setUserId(int userId) { this.userId = userId; }

    /** @return borrowing identifier */
    public int getBorrowId() { return borrowId; }

    /** @param borrowId borrowing identifier */
    public void setBorrowId(int borrowId) { this.borrowId = borrowId; }

    /** @return current fine amount */
    public double getAmount() { return amount; }

    /** @param amount current fine amount */
    public void setAmount(double amount) { this.amount = amount; }

    /** @return true if the fine is fully paid */
    public boolean isPaid() { return paid; }

    /** @param paid payment flag */
    public void setPaid(boolean paid) { this.paid = paid; }

    /** @return date of the fine record */
    public LocalDate getFineDate() { return fineDate; }

    /** @param fineDate date of the fine record */
    public void setFineDate(LocalDate fineDate) { this.fineDate = fineDate; }
}
