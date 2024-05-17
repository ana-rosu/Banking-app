package dao;

import interfaces.GenericDAO;
import model.account.Account;
import model.transaction.Transaction;
import model.transaction.TransactionType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO implements GenericDAO<Transaction> {
    private Connection connection;
    public TransactionDAO(Connection connection){
        this.connection = connection;
    }
    @Override
    public void create(Transaction transaction) {
        String sql = "INSERT INTO transaction (fromIBAN, toIBAN, transactionDate, amount, descrip, transactionType, accountId) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            this.setParameters(stmt, transaction.getFromIBAN(), transaction.getToIBAN(), transaction.getDate(), transaction.getAmount(), transaction.getDescription(), transaction.getType().name(), transaction.getAccountId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 1) {
                try (Statement queryStmt = connection.createStatement();
                     ResultSet rs = queryStmt.executeQuery("SELECT transaction_id_seq.CURRVAL FROM dual")) {
                    if (rs.next()) {
                        int generatedId = rs.getInt(1);
                        transaction.setId(generatedId);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating transaction: " + e.getMessage());
        }
    }
    @Override
    public Transaction read(int id) {
        String sql = "SELECT * FROM transaction WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    return new Transaction(resultSet.getInt("id"), resultSet.getString("fromIBAN"), resultSet.getString("toIBAN"), resultSet.getDate("transactionDate"), resultSet.getDouble("amount"), resultSet.getString("descrip"), TransactionType.valueOf(resultSet.getString("transactionType")), resultSet.getInt("accountId")); // create an Transaction object from the retrieved data
                } else {
                    return null;  // transaction with the specified id was not found
                }
            }
        } catch (SQLException e) {
            System.err.println("Error reading transaction: " + e.getMessage());
            return null;
        }
    }
    public List<Transaction> selectAllWhereAccId(int accId) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM Transaction WHERE accId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, accId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Transaction transaction = new Transaction(rs.getInt("id"), rs.getString("fromIBAN"), rs.getString("toIBAN"), rs.getDate("transactionDate"), rs.getDouble("amount"), rs.getString("descrip"), TransactionType.valueOf(rs.getString("transactionType")), rs.getInt("accountId"));
                    transactions.add(transaction);
                }
            }
        }
        return transactions;
    }
}
