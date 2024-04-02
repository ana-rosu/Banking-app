package interfaces;
import model.account.Account;
public interface Transactionable {
    void deposit(double amount);
    void withdraw(double amount);
    void transfer(Account destination, double amount);
}
