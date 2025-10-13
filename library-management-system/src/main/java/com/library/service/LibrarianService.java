package com.library.service;

import com.library.dao.BorrowingDAO;
import com.library.dao.FineDAO;
import com.library.model.Borrowing;

import java.util.List;

public class LibrarianService {
    private final BorrowingDAO borrowingDAO = new BorrowingDAO();
    private final FineDAO fineDAO = new FineDAO();

    
    public void detectOverdueBooks() throws Exception {
        List<Borrowing> overdueList = borrowingDAO.findOverdueBooks();
        for (Borrowing b : overdueList) {
            fineDAO.issueFine(b.getBorrowId(), b.getUserId(), 10);
        }
    }
}
