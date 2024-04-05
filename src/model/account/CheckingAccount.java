package model.account;

//used for everyday transactions such as paying bills, making purchases, withdrawing cash
//features: check-writing, debit cards, and to manage recurring expenses such as utility bills, rent or mortgage payments, and subscriptions
//no restrictions on the number of transactions
public class CheckingAccount extends Account {
    public CheckingAccount(double balance){
        super(balance);
    }
    @Override
    public String toString() {
        return "CheckingAccount{" +
                "id=" + id +
                ", IBAN='" + IBAN + '\'' +
                ", balance=" + balance +
                ", transactionHistory=" + transactionHistory +
                ", linkedCard=" + linkedCard +
                '}';
    }
}
