package model.account;

import model.transaction.Transaction;
import model.transaction.TransactionType;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

//intended for saving money over the long term by storing and accumulating funds over time
//restrictions on the amount of withdrawal you can make per month
//insurance up to certain limits, providing safety and security for deposited funds
public class SavingsAccount extends Account{
    private final Date startDate, endDate;
    private final double interestRate;
    private Timer interestTimer; // timer for scheduling callbacks
    private final double depositLimit;
    private final double withdrawalLimitPerMonth;
    private int lastWithdrawalMonth;
    private double totalWithdrawalAmountThisMonth;
    private double transferFees;


    public SavingsAccount(double balance, Date startDate, Date endDate) {
        super(balance);
        this.startDate = startDate;
        this.endDate = endDate;
        this.interestRate = 0.05;
        this.depositLimit = 100000;
        this.transferFees = 0.07;
        this.withdrawalLimitPerMonth = 1000;
        this.lastWithdrawalMonth = -1;
        this.interestTimer = new Timer();
        scheduleInterestCallbacks(); // schedule interest callbacks every other 3 months
    }

    public SavingsAccount(double balance, Date startDate, Date endDate, double interestRate, double depositLimit, double withdrawalLimitPerMonth) {
        super(balance);
        this.startDate = startDate;
        this.endDate = endDate;
        this.interestRate = interestRate;
        this.depositLimit = depositLimit;
        this.withdrawalLimitPerMonth = withdrawalLimitPerMonth;
        this.interestTimer = new Timer();
        scheduleInterestCallbacks();
        lastWithdrawalMonth = -1;
    }
    private void scheduleInterestCallbacks() {
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);

        int nextQuarterMonth = (currentMonth + 3) % 12;
        calendar.set(Calendar.MONTH, nextQuarterMonth);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        interestTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                applyInterest();
                scheduleInterestCallbacks();
            }
        }, calendar.getTime());
    }
    @Override
    public void deposit(double amount) {
        if (isDepositLimitExceeded(amount)) {
            System.out.println("Deposit limit exceeded. Cannot deposit more funds.");
            return;
        }
        if (amount > 0 && (balance + amount) <= depositLimit) {
            balance += amount;
            Transaction transaction = new Transaction(this.getIBAN(), this.getIBAN(), amount, "Deposit", new Date(), TransactionType.DEPOSIT);
            transactionHistory.add(transaction);
            System.out.println("Deposited $" + amount + " into Savings Account.");
        } else {
            System.out.println("Invalid deposit amount or exceeding deposit limit.");
        }
    }
    public void applyInterest() {
        double quarterlyInterestRate = interestRate / 4;
        double quarterlyInterest = balance * quarterlyInterestRate;
        balance += quarterlyInterest;
        System.out.println("Interest applied: $" + quarterlyInterest);
    }
    private boolean isDepositLimitExceeded(double amount) {
        return (balance + amount) > depositLimit;
    }
    @Override
    public boolean withdraw(double amount) {
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);

        if(currentMonth == lastWithdrawalMonth)
            totalWithdrawalAmountThisMonth += amount;
        else
            totalWithdrawalAmountThisMonth = 0;

        lastWithdrawalMonth = currentMonth;

        if (totalWithdrawalAmountThisMonth <= withdrawalLimitPerMonth) {
            return super.withdraw(amount);
        } else {
            System.out.println("Total withdrawal amount for this month exceeded. Try again next month!");
            return false;
        }
    }
    @Override
    public void transfer(Account destination, double amount) {
        super.transfer(destination, amount + this.transferFees);
    }
    @Override
    public void setAccountStatus(AccountStatus status){
        if(status == AccountStatus.OPEN){
            this.interestTimer = new Timer();
            scheduleInterestCallbacks();
            return;
        }
        if (status == AccountStatus.CLOSED){
            interestTimer.cancel();
        }
    }
    @Override
    public String toString() {
        return "SavingsAccount{id=" + id  +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", interestRate=" + interestRate +
                ", depositLimit=" + depositLimit +
                ", withdrawalLimitPerMonth=" + withdrawalLimitPerMonth +
                ", IBAN='" + IBAN + '\'' +
                ", balance=" + balance +
                ", transactionHistory=" + transactionHistory +
                ", linkedCard=" + linkedCard +
                '}';
    }
}
