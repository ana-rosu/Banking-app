package dao;

import model.account.Account;
import model.card.Card;
import model.transaction.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {
    private Connection connection;
    public AccountDAO(Connection connection){
        this.connection = connection;
    }
    public List<Account> selectAllWhereUserId(int userId) throws SQLException {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM Account WHERE userId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
//                    Account account = extractAccountFromResultSet(rs);
//                    accounts.add(account);
                }
            }
        }
        return accounts;
    }
    protected Card getLinkedCard(int cardId) throws SQLException {
        CardDAO cardDAO = new CardDAO(connection);
        return cardDAO.read(cardId);
    }
    protected List<Transaction> getTransactionsForAccount(int accId) throws SQLException {
        TransactionDAO transactionDAO = new TransactionDAO(connection);
        return transactionDAO.selectAllWhereAccId(accId);
    }
}
