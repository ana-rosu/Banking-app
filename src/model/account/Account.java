package model.account;

import model.transaction.Transaction;
import interfaces.Transactionable;
import interfaces.Accountable;
import java.util.List;
import java.util.Random;

public abstract class Account implements Transactionable, Accountable {
    protected int id;
    static int contorId = 1;
    protected String IBAN;
    protected double balance;
    protected List<Transaction> transactionHistory;

    {
        this.id = contorId++;
    }
    public Account(double balance) {
        this.IBAN = generateIban();
        this.balance = balance;
    }
    private String generateIban() {
        String countryCode = "RO";
        String bankCode = "BNK";
        String accountNumber = generateRandom10DigitsNumber();
        return countryCode + bankCode + accountNumber;
    }
    private String generateRandom10DigitsNumber(){
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
    @Override
    public double getBalance() {
        return balance;
    }

    @Override
    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getIBAN() {
        return IBAN;
    }

    public List<Transaction> getTransactionHistory() {
        return transactionHistory;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", IBAN='" + IBAN + '\'' +
                ", balance=" + balance +
                ", transactionHistory=" + transactionHistory +
                '}';
    }
    public void addTransaction(Transaction transaction){
        this.transactionHistory.add(transaction);
    }
}
