package com.library.model;

import java.time.LocalDate;

public class Fine {

    private int id;
    private int userId;
    private int borrowId;
    private double amount;
    private boolean paid;
    private LocalDate fineDate;

    
    public Fine() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getBorrowId() {
        return borrowId;
    }

    public void setBorrowId(int borrowId) {
        this.borrowId = borrowId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

	public LocalDate getFineDate() {
		return fineDate;
	}

	public void setFineDate(LocalDate fineDate) {
		this.fineDate = fineDate;
	}

    /**
     * Checks whether this fine is still pending payment.
     * @return true if unpaid
     */
    public boolean isUnpaid() {
        return !paid;
    }

}
