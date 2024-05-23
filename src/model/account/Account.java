package model.account;

import model.card.Card;
import model.transaction.Transaction;
import interfaces.Transactionable;
import model.transaction.TransactionType;

import java.util.*;

public abstract class Account implements Transactionable {
    protected int id;
    protected String IBAN;
    protected double balance;
    protected AccountStatus accountStatus;
    protected int userId;
    protected Card linkedCard;
    protected List<Transaction> transactionHistory;
    static private Set<String> usedIBAN = new HashSet<>();

    public Account(int id, String IBAN, Double balance, AccountStatus accountStatus, int userId, Card linkedCard, List<Transaction> transactionHistory) {
       this.id = id;
       this.IBAN = IBAN;
       this.balance = balance;
       this.accountStatus = accountStatus;
       this.userId = userId;
       this.linkedCard = linkedCard;
       this.transactionHistory = transactionHistory;
    }


    public Account(double balance) {
        String generatedIBAN = this.generateIban();
        while(usedIBAN.contains(generatedIBAN))
            generatedIBAN = this.generateIban();
        this.IBAN = generatedIBAN;
        usedIBAN.add(this.IBAN);

        this.balance = balance;
        this.accountStatus = AccountStatus.OPEN;
        this.transactionHistory = new ArrayList<>();
    }

    @Override
    public void deposit(double amount) {
       deposit(amount, false);
    }
    public void deposit(double amount, boolean isTransfer) {
        if (amount > 0) {
            setBalance(balance + amount);
            if(!isTransfer) {
                Transaction transaction = new Transaction(this.getIBAN(), this.getIBAN(), amount, "Deposit", new Date(), TransactionType.DEPOSIT, this.id);
                transactionHistory.add(transaction);
            }
        }
    }
    @Override
    public boolean withdraw(double amount) {
        return withdraw(amount, false);
    }
    public boolean withdraw(double amount, boolean isTransfer) {
        if (amount > 0 && amount > balance) {
            System.out.println("Insufficient funds.");
            return false;
        }
        this.balance -= amount;
        if(!isTransfer) {
            Transaction transaction = new Transaction(this.getIBAN(), this.getIBAN(), amount, "Withdrawal", new Date(), TransactionType.WITHDRAWAL, this.id);
            this.transactionHistory.add(transaction);
        }
        return true;
    }

    public void transfer(Account destination, double amount) {
        if (amount > 0 && this.withdraw(amount, true)) {
            destination.deposit(amount, true);
            System.out.println("Transfer successful.");
            Transaction toDest = new Transaction(this.getIBAN(), destination.getIBAN(), amount, "Transfer to " + destination.getIBAN(), new Date(), TransactionType.TRANSFER, this.id);
            Transaction fromSource = new Transaction(this.getIBAN(), destination.getIBAN(), amount, "Transfer from " + this.getIBAN(), new Date(), TransactionType.TRANSFER, destination.getId());
            transactionHistory.add(toDest);
            destination.transactionHistory.add(fromSource);
        } else {
            System.out.println("Transfer failed. Insufficient funds.");
        }
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
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getBalance() {
        return balance;
    }
    public String getIBAN() {
        return IBAN;
    }
    public List<Transaction> getTransactionHistory() {
        return transactionHistory;
    }
    public Card getLinkedCard() {
        return linkedCard;
    }
    public void setLinkedCard(Card linkedCard) {
        this.linkedCard = linkedCard;
    }
    public void setBalance(double balance) {
        this.balance = balance;
    }
    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setTransactionHistory(List<Transaction> transactionHistory) {
        this.transactionHistory = transactionHistory;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", IBAN='" + IBAN + '\'' +
                ", balance=" + balance +
                ", transactionHistory=" + transactionHistory +
                ", linkedCard=" + linkedCard +
                ", accountStatus=" + accountStatus +
                '}';
    }
}
