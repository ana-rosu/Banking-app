package model.account;

//used for everyday transactions such as paying bills, making purchases, withdrawing cash
//features: check-writing, debit cards, and to manage recurring expenses such as utility bills, rent or mortgage payments, and subscriptions
//no restrictions on the number of transactions
public class CheckingAccount extends Account {
    public CheckingAccount(double balance){
        super(balance);
    }
    @Override
    public void deposit(double amount) {
        setBalance(getBalance() + amount);
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
