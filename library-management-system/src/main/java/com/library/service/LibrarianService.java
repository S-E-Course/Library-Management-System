package com.library.service;

import com.library.dao.BorrowingDAO;
import com.library.dao.FineDAO;
import com.library.model.Book;
import com.library.model.Borrowing;

import java.util.List;

public class LibrarianService {
    private final BorrowingDAO borrowingDAO = new BorrowingDAO();
    private final FineDAO fineDAO = new FineDAO();

    
    public void detectOverdueBooks() throws Exception {
        List<Borrowing> overdueList = borrowingDAO.findOverdueBooks();
        for (Borrowing b : overdueList) {
            boolean success = fineDAO.issueFine(b.getBorrowId(), b.getUserId(), 10);
            if (success) {
                System.out.println("Fine issued for borrowId=" + b.getBorrowId());
            } else {
                System.out.println("Failed to issue fine for borrowId=" + b.getBorrowId());
            }
        }
    }


	public boolean addBook(String title, String author, String isbn) {
		// TODO Auto-generated method stub
		return false;
	}


	public boolean removeBook(int bookId) {
		// TODO Auto-generated method stub
		return false;
	}


	public List<Book> listBooks() {
		// TODO Auto-generated method stub
		return null;
	}


	public boolean setBookAvailability(int bookId, boolean b) {
		// TODO Auto-generated method stub
		return false;
	}



	public boolean login(String username, String password) {
		// TODO Auto-generated method stub
		return false;
	}


	public void logout() {
		// TODO Auto-generated method stub
		
	}
}
