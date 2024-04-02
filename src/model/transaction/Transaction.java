package model.transaction;
import java.util.Date;

public class Transaction {
    private final String fromIBAN, toIBAN;
    private final double amount;
    private final String description;
    private final Date date;
    private final TransactionType type;

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

    public TransactionType getType() {
        return type;
    }

    public Date getDate() {
        return date;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }
}
