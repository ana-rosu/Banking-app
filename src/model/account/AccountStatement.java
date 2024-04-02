package model.account;

import model.transaction.Transaction;

import java.util.Date;
import java.util.List;

public class AccountStatement {
    private Account account;
    private List<Transaction> transactions;

    public AccountStatement(Account account, List<Transaction> transactions) {
        this.account = account;
        this.transactions = transactions;
    }

    public void generateStatement(Date fromDate, Date toDate) {
        System.out.println("Account Statement for: " + account.getIBAN());
        System.out.println("------------------------------");
        System.out.println("Date\t\tType\t\tAmount\t\tDescription");
        System.out.println("------------------------------");
        for (Transaction transaction : transactions) {
            if(transaction.getDate().after(fromDate) && transaction.getDate().before(toDate))
                System.out.println(transaction.getDate() + "\t" + transaction.getType() + "\t\t" + transaction.getAmount() + "\t\t" + transaction.getDescription());
        }
        System.out.println("------------------------------");
    }
}
