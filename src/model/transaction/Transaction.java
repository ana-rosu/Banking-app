package model.transaction;
import java.util.Date;

public class Transaction {
    private int id;
    private int accountId;
    private final String fromIBAN, toIBAN;
    private final double amount;
    private final String description;
    private final Date date;
    private final TransactionType type;

    public Transaction(int id, String fromIBAN, String toIBAN, Date date, double amount, String description, TransactionType type, int accountId) {
        this.id = id;
        this.fromIBAN = fromIBAN;
        this.toIBAN = toIBAN;
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.type = type;
        this.accountId = accountId;
    }

    public Transaction(String fromIBAN, String toIBAN, double amount, String description, Date date, TransactionType type) {
        this.fromIBAN = fromIBAN;
        this.toIBAN = toIBAN;
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "fromIBAN='" + fromIBAN + '\'' +
                ", toIBAN='" + toIBAN + '\'' +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", date=" + date +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getFromIBAN() {
        return fromIBAN;
    }

    public String getToIBAN() {
        return toIBAN;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public Date getDate() {
        return date;
    }

    public TransactionType getType() {
        return type;
    }
}
