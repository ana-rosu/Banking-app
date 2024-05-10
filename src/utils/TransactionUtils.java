package utils;

import model.transaction.Transaction;
import model.transaction.TransactionType;

import java.text.SimpleDateFormat;
import java.util.*;
import java.text.ParseException;

public class TransactionUtils {
    public static void showFilteredTransactions(List<Transaction> transactions, TransactionType type) {
        System.out.println(type + " Transactions:");
        for (Transaction transaction : transactions) {
            if (transaction.getType() == type) {
                System.out.println(transaction);
            }
        }
    }
    public static void sortByDateAscending(List<Transaction> transactions) {
        Collections.sort(transactions, Comparator.comparing(Transaction::getDate));
        System.out.println("Transactions sorted by date (ascending):");
        displayTransactions(transactions);
    }
    public static void sortByDateDescending(List<Transaction> transactions) {
        Collections.sort(transactions, Comparator.comparing(Transaction::getDate).reversed());
        System.out.println("Transactions sorted by date (descending):");
        displayTransactions(transactions);
    }
    public static void sortByAmountAscending(List<Transaction> transactions) {
        Collections.sort(transactions, Comparator.comparing(Transaction::getAmount));
        System.out.println("Transactions sorted by amount (ascending):");
        displayTransactions(transactions);
    }
    public static void sortByAmountDescending(List<Transaction> transactions) {
        Collections.sort(transactions, Comparator.comparing(Transaction::getAmount).reversed());
        System.out.println("Transactions sorted by amount (descending):");
        displayTransactions(transactions);
    }
    public static void searchByDate(List<Transaction> transactions, String dateString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date searchDate = dateFormat.parse(dateString);
            if (searchDate.after(new Date())) {
                System.out.println("The date cannot be in the future. Please try again.");
                return;
            }
            List<Transaction> showTransactions = new ArrayList<>();
            for (Transaction transaction : transactions) {
                if (dateFormat.format(transaction.getDate()).equals(dateFormat.format(searchDate))) {
                    showTransactions.add(transaction);
                }
            }
            if (showTransactions.isEmpty()){
                System.out.println("No transactions on" + dateString);
            }
            else{
                System.out.println("Transactions on " + dateString + ":");
                for(Transaction transaction : showTransactions)
                    System.out.println(transaction);
            }
        } catch (ParseException e) {
            System.out.println("Invalid date format. Please use yyyy-mm-dd.");
        }
    }

    public static void displayTransactions(List<Transaction> transactions) {
        if (transactions.isEmpty()) {
            System.out.println("No transactions available.");
        } else {
            for (Transaction transaction : transactions) {
                System.out.println(transaction);
            }
        }
    }

}
