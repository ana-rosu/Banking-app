package dao;

import interfaces.GenericDAO;
import model.account.Account;
import model.account.AccountStatus;
import model.account.CheckingAccount;
import model.account.SavingsAccount;
import model.card.Card;
import model.transaction.Transaction;
import model.user.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountDAO implements GenericDAO<Account> {
    private Connection connection;
    private CardDAO cardDAO;
    private TransactionDAO transactionDAO;
    public AccountDAO(Connection connection, CardDAO cardDAO, TransactionDAO transactionDAO){
        this.connection = connection;
        this.cardDAO = cardDAO;
        this.transactionDAO = transactionDAO;
    }

    public void create(Account account){
        String sql = "INSERT INTO Account (IBAN, balance, accountStatus, userId, cardId) VALUES (?, ?, ?, ?, ?)";
        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setString(1, account.getIBAN());
            stmt.setDouble(2, account.getBalance());
            stmt.setString(3, account.getAccountStatus().name());
            stmt.setInt(4, account.getUserId());
            if (account.getLinkedCard() != null) {
                stmt.setInt(5, account.getLinkedCard().getId());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 1) {
                try (Statement queryStmt = connection.createStatement();
                     ResultSet rs = queryStmt.executeQuery("SELECT c##anar.account_id_seq.CURRVAL FROM dual")) {
                    if (rs.next()) {
                        int generatedId = rs.getInt(1);
                        account.setId(generatedId);

                        // insert transactions
                        for (Transaction transaction : account.getTransactionHistory()) {
                            if (transaction.getId() == 0) {
                                transactionDAO.create(transaction);
                            }
                        }
                        // insert its linked card if it was emitted
                        if (account.getLinkedCard() != null){
                            cardDAO.create(account.getLinkedCard());
                        }
                        if (account instanceof CheckingAccount) {
                            insertCheckingAccount((CheckingAccount) account);
                        } else {
                            insertSavingsAccount((SavingsAccount) account);
                        }
                    }
                }
            }
        }

        catch(SQLException e){
            System.err.println("Error creating account: " + e.getMessage());
        }
    }

    private void insertCheckingAccount(CheckingAccount account) throws SQLException {
        String sql = "INSERT INTO CheckingAccount (accountId) VALUES (?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, account.getId());
            stmt.executeUpdate();
        }
    }
    private void insertSavingsAccount(SavingsAccount account) throws SQLException {
        String sql = "INSERT INTO SavingsAccount (accountId, startDate, endDate, interestRate, depositLimit, " +
                "withdrawalLimitPerMonth, lastWithdrawalMonth, totalWithdrawalAmountThisMonth, transferFees) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            setParameters(stmt, account.getId(), new java.sql.Date(account.getStartDate().getTime()), new java.sql.Date(account.getEndDate().getTime()), account.getInterestRate(),
                    account.getDepositLimit(), account.getWithdrawalLimitPerMonth(), account.getLastWithdrawalMonth(),
                    account.getTotalWithdrawalAmountThisMonth(), account.getTransferFees());
            stmt.executeUpdate();
        }
    }
    public Account read(int id) {
        String sqlChecking =
                        "SELECT * " +
                        "FROM Account a " +
                        "JOIN CheckingAccount ca ON a.id = ca.accountId " +
                        "WHERE a.id = ?";

        String sqlSavings =
                        "SELECT * " +
                        "FROM Account a " +
                        "JOIN SavingsAccount sa ON a.id = sa.accountId " +
                        "WHERE a.id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sqlChecking)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Card linkedCard = cardDAO.read(rs.getInt("cardId"));
                    List<Transaction> transactions = transactionDAO.selectAllWhereAccId(id);;
                    return new CheckingAccount(
                            rs.getInt("id"),
                            rs.getString("IBAN"),
                            rs.getDouble("balance"),
                            AccountStatus.valueOf(rs.getString("accountStatus")),
                            rs.getInt("userId"),
                            linkedCard,
                            transactions);
                }
            }
        }
        catch (SQLException e){
            System.err.println(e.getMessage());
        }

        try (PreparedStatement stmt = connection.prepareStatement(sqlSavings)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Card linkedCard = cardDAO.read(rs.getInt("cardId"));
                    List<Transaction> transactions = transactionDAO.selectAllWhereAccId(id);;
                    return new SavingsAccount(
                            rs.getInt("id"),
                            rs.getString("IBAN"),
                            rs.getDouble("balance"),
                            AccountStatus.valueOf(rs.getString("accountStatus")),
                            rs.getInt("userId"),
                            linkedCard,
                            transactions,
                            rs.getDate("startDate"),
                            rs.getDate("endDate"),
                            rs.getDouble("interestRate"),
                            rs.getDouble("depositLimit"),
                            rs.getDouble("withdrawalLimitPerMonth"),
                            rs.getDouble("transferFees"));
                }
            }
        }
        catch (SQLException e){
            System.err.println(e.getMessage());
        }
        return null;
    }
    public void update(Account account){
        String sql = "UPDATE Account SET IBAN = ?, balance = ?, accountStatus = ?, userId = ?, cardId = ? WHERE id = ?";
        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            // check if linkedCard is not null before setting cardId
            if (account.getLinkedCard() != null) {
                this.setParameters(stmt, account.getIBAN(), account.getBalance(), account.getAccountStatus().name(), account.getUserId(), account.getLinkedCard().getId(), account.getId());
            } else {
                // if linkedCard is null, set cardId to null
                stmt.setString(1, account.getIBAN());
                stmt.setDouble(2, account.getBalance());
                stmt.setString(3, account.getAccountStatus().name());
                stmt.setInt(4, account.getUserId());
                stmt.setNull(5, java.sql.Types.INTEGER);
                stmt.setInt(6, account.getId());
            }

            stmt.executeUpdate();

            // insert new transactions if transactionHistory has been updated
            for (Transaction transaction : account.getTransactionHistory()) {
                if (transaction.getId() == 0) {
                    transactionDAO.create(transaction);
                }
            }
            if (account instanceof SavingsAccount) {
                updateSavingsAccount((SavingsAccount) account);
            }
        } catch(SQLException e){
            System.err.println("Error updating account: " + e.getMessage());
        }
    }

    private void updateSavingsAccount(SavingsAccount account) throws SQLException {
        String sql = "UPDATE SavingsAccount SET startDate = ?, endDate = ?, interestRate = ?, depositLimit = ?, " +
                "withdrawalLimitPerMonth = ?, lastWithdrawalMonth = ?, totalWithdrawalAmountThisMonth = ?, " +
                "transferFees = ? WHERE accountId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            setParameters(stmt, new java.sql.Date(account.getStartDate().getTime()), new java.sql.Date(account.getEndDate().getTime()), account.getInterestRate(),
                    account.getDepositLimit(), account.getWithdrawalLimitPerMonth(), account.getLastWithdrawalMonth(),
                    account.getTotalWithdrawalAmountThisMonth(), account.getTransferFees(), account.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating savings account: " + e.getMessage());
            throw e;
        }
    }
    public void delete(Account account){
        String sql = "DELETE FROM Account WHERE id = ?";
        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1, account.getId());
            stmt.executeUpdate();

            if(account instanceof CheckingAccount){
                deleteFromCheckingAccount(account.getId());
            } else {
                deleteFromSavingsAccount(account.getId());
            }
            //delete linked card if it was emitted
            if (account.getLinkedCard() != null){
                cardDAO.delete(account.getLinkedCard().getId());
            }
            //delete transactions associated with this account
            for (Transaction transaction : account.getTransactionHistory()){
                transactionDAO.delete(transaction.getId());
            }
        }
        catch(SQLException e){
            System.err.println("Error deleting account: " + e.getMessage());
        }
    }
    private void deleteFromCheckingAccount(int id) throws SQLException {
        String sql = "DELETE FROM CheckingAccount WHERE accountId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting from CheckingAccount: " + e.getMessage());
            throw e;
        }
    }
    private void deleteFromSavingsAccount(int id) throws SQLException {
        String sql = "DELETE FROM SavingsAccount WHERE accountId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting from SavingsAccount: " + e.getMessage());
            throw e;
        }
    }
    public List<Account> selectAllWhereUserId(int userId) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT id FROM Account WHERE userId = ?";
        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1, userId);
            try(ResultSet rs = stmt.executeQuery()){
                while(rs.next()){
                    int id = rs.getInt(1);
                    accounts.add(this.read(id));
                }
            }
        }catch(SQLException e){
            System.err.println("Error selecting accounts associated with user: " + e.getMessage());
        }
        return accounts;
    }

    public Map<Integer, Account> getAllAccounts() {
        Map<Integer, Account> accounts = new HashMap<>();
        String sql = "SELECT id FROM Account";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()){
                int id = rs.getInt(1);
                accounts.put(id, this.read(id));
            }
        }
        catch(SQLException e){
            System.err.println("Error fetching accounts: " + e.getMessage());
        }
        return accounts;
    }
    public List<Transaction> generateTransactionsForStatement(Date fromDate, Date toDate, int id){
        return transactionDAO.getTransactionsBetweenDatesForAcc(fromDate, toDate, id);
    }
}