package interfaces;
import model.account.Account;
public interface Transactionable {
    void deposit(double amount);
    boolean withdraw(double amount);
    void transfer(Account destination, double amount);
}
