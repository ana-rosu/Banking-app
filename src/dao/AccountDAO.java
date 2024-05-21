package dao;

import interfaces.GenericDAO;
import model.account.Account;
import model.account.AccountStatus;
import model.account.CheckingAccount;
import model.account.SavingsAccount;
import model.card.Card;
import model.transaction.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO implements GenericDAO<Account> {
    private Connection connection;
    public AccountDAO(Connection connection){
        this.connection = connection;
    }

    public void create(Account account){
        String sql = "INSERT INTO Account (IBAN, balance, accountStatus, userId, cardId) VALUES (?, ?, ?, ?, ?)";
        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            setParameters(stmt, account.getIBAN(), account.getBalance(), account.getAccountStatus().name(), account.getUserId(), account.getLinkedCard().getId());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 1) {
                try (Statement queryStmt = connection.createStatement();
                     ResultSet rs = queryStmt.executeQuery("SELECT account_id_seq.CURRVAL FROM dual")) {
                    if (rs.next()) {
                        int generatedId = rs.getInt(1);
                        account.setId(generatedId);

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
            System.err.println(e.getMessage());
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
            setParameters(stmt, account.getId(), account.getStartDate(), account.getEndDate(), account.getInterestRate(),
                    account.getDepositLimit(), account.getWithdrawalLimitPerMonth(), account.getLastWithdrawalMonth(),
                    account.getTotalWithdrawalAmountThisMonth(), account.getTransferFees());
            stmt.executeUpdate();
        }
    }
    public Account read(int id) {
        String sqlChecking =
                "SELECT a.id, a.IBAN, a.balance, a.accountStatus, a.userId, a.cardId " +
                        "FROM Account a " +
                        "JOIN CheckingAccount ca ON a.id = ca.accountId " +
                        "WHERE a.id = ?";

        String sqlSavings =
                "SELECT a.id, a.IBAN, a.balance, a.accountStatus, a.userId, a.cardId, " +
                        "sa.startDate, sa.endDate, sa.interestRate, sa.depositLimit, " +
                        "sa.withdrawalLimitPerMonth, sa.lastWithdrawalMonth, sa.totalWithdrawalAmountThisMonth, sa.transferFees " +
                        "FROM Account a " +
                        "JOIN SavingsAccount sa ON a.id = sa.accountId " +
                        "WHERE a.id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sqlChecking)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Card linkedCard = getLinkedCard(rs.getInt("cardId"));
                    List<Transaction> transactions = getTransactionsForAccount(id);
                    CheckingAccount ca = new CheckingAccount(
                            rs.getInt("id"),
                            rs.getString("IBAN"),
                            rs.getDouble("balance"),
                            AccountStatus.valueOf(rs.getString("accountStatus")),
                            rs.getInt("userId"),
                            linkedCard,
                            transactions);
                    return ca;
                }
            }
        }
        catch (SQLException e){
            System.err.println(e.getMessage());
        }

        try (PreparedStatement stmt = connection.prepareStatement(sqlSavings)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Card linkedCard = getLinkedCard(rs.getInt("cardId"));
                    List<Transaction> transactions = getTransactionsForAccount(id);
                    SavingsAccount sa = new SavingsAccount(
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
                    return sa;
                }
            }
        }
        catch (SQLException e){
            System.err.println(e.getMessage());
        }
        return null;
    }
    protected Card getLinkedCard(int cardId) {
        CardDAO cardDAO = new CardDAO(connection);
        return cardDAO.read(cardId);
    }
    protected List<Transaction> getTransactionsForAccount(int accId) throws SQLException {
        TransactionDAO transactionDAO = new TransactionDAO(connection);
        return transactionDAO.selectAllWhereAccId(accId);
    }

    public void update(Account account){
        String sql = "UPDATE Account SET IBAN = ?, balance = ?, accountStatus = ?, userId = ?, cardId = ? WHERE id = ?";
        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            this.setParameters(stmt, account.getIBAN(), account.getBalance(), account.getAccountStatus(), account.getUserId(), account.getLinkedCard().getId(), account.getId());
            stmt.executeUpdate();
            if (account instanceof SavingsAccount) {
                updateSavingsAccount((SavingsAccount) account);
            }
        }catch(SQLException e){
            System.err.println("Error updating account: " + e.getMessage());
        }
    }
    private void updateSavingsAccount(SavingsAccount account) throws SQLException {
        String sql = "UPDATE SavingsAccount SET startDate = ?, endDate = ?, interestRate = ?, depositLimit = ?, " +
                "withdrawalLimitPerMonth = ?, lastWithdrawalMonth = ?, totalWithdrawalAmountThisMonth = ?, " +
                "transferFees = ? WHERE accountId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            setParameters(stmt, account.getStartDate(), account.getEndDate(), account.getInterestRate(),
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
    public List<Account> selectAllWhereUserId(int userId) throws SQLException {
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
        }
        return accounts;
    }
}