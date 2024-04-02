package model.account;

import java.time.LocalDate;
import java.util.Date;

//used for storing and accumulating funds over time
//restrictions on the number of withdrawals or transfers you can make per month
//insurance up to certain limits, providing safety and security for deposited funds
public class SavingsAccount extends Account{
    private final Date startDate, endDate;
    private final double interestRate;
    private final double depositLimit;
    private final double withdrawalLimitPerMonth;

    public SavingsAccount(double balance, Date startDate, Date endDate, double interestRate, double depositLimit, double withdrawalLimitPerMonth) {
        super(balance);
        this.startDate = startDate;
        this.endDate = endDate;
        this.interestRate = interestRate;
        this.depositLimit = depositLimit;
        this.withdrawalLimitPerMonth = withdrawalLimitPerMonth;
    }
    @Override
    public void deposit(double amount) {
        if (isDepositLimitExceeded(amount)) {
            System.out.println("Deposit limit exceeded. Cannot deposit more funds.");
            return;
        }
        setBalance(getBalance() + amount);
//        lastInterestCalculationDate = LocalDate.now();
        calculateInterest();
    }
    private void calculateInterest(){
        double balance = getBalance();
        double annualInterestRate = 0.05;
        double interestAmount = balance * (annualInterestRate / 100);
        System.out.println("Interest calculated: $" + interestAmount);
    }
    private boolean isDepositLimitExceeded(double amount) {
        return (getBalance() + amount) > getDepositLimit();
    }
    private double getDepositLimit() {
        return this.depositLimit;
    }
    @Override
    public void withdraw(double amount) {
        // Implement withdraw logic
    }
    @Override
    public void transfer(Account destination, double amount) {
        // Implement transfer logic
    }
    @Override
    public double getBalance() {
        // Implement getBalance logic
        return balance;
    }

    @Override
    public void setBalance(double balance) {
        // Implement setBalance logic
        this.balance = balance;
    }
}
