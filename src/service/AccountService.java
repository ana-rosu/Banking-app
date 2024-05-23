package service;

import dao.AccountDAO;
import dao.TransactionDAO;
import dao.UserDAO;
import model.account.Account;
import model.account.AccountType;
import model.account.CheckingAccount;
import model.account.SavingsAccount;
import model.transaction.Transaction;
import model.user.User;

import java.util.Date;
import java.util.List;
import java.util.Map;

// manages all accounts of a user
public class AccountService {
    private AccountDAO accountDAO;
    private UserDAO userDAO;
    private AuditService auditService;

    public AccountService(AccountDAO accountDAO, UserDAO userDAO){
        this.accountDAO = accountDAO;
        this.userDAO = userDAO;
        this.auditService = AuditService.getInstance();
    }
    public Account getAccountById(int accountId){
        return accountDAO.read(accountId);
    }
    public List<Account> getAllAccounts(int userId){
        return accountDAO.selectAllWhereUserId(userId);
    }
    public Account getAccount(int userId, int accountId){
        User user = userDAO.read(userId);
        List<Account> accounts = user.getAccountList();
        for(Account acc : accounts){
            if(accountId == acc.getId()){
                return acc;
            }
        }
        return null;
    }
    public String viewAllAccounts(int userId) {
        User user = userDAO.read(userId);
        List<Account> accounts = user.getAccountList();
        StringBuilder sb = new StringBuilder();
        for (Account account : accounts) {
            sb.append(account);
            sb.append("\n");
        }
        auditService.logAction(String.format("user_%s_viewed_all_accounts", userId));
        return sb.toString();
    }
    public void openNewAccount(int userId, AccountType accType){
        Account account;
        if (accType == AccountType.CHECKING) {
            account = new CheckingAccount(0);
            account.setUserId(userId);
        } else {
            Date startDate = new Date();
            Date endDate = new Date(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000);
            account = new SavingsAccount(0, startDate, endDate);
            account.setUserId(userId);
        }
        User user = userDAO.read(userId);
        List<Account> accounts = user.getAccountList();
        accounts.add(account);
        user.setAccountList(accounts);

        accountDAO.create(account);
        userDAO.update(user);
        auditService.logAction(String.format("user_%s_opened_new_account", userId));
    }

    public void generateStatement(Date fromDate, Date toDate, int accountId, int userId) {

        List<Transaction> transactions = accountDAO.generateTransactionsForStatement(new java.sql.Date(fromDate.getTime()), new java.sql.Date(toDate.getTime()), accountId);
        if (transactions == null){
            System.out.println("No transactions found in this period!");
            return;
        }
        Account account = getAccount(userId, accountId);
        System.out.println("Account Statement for: " + account.getIBAN());
        System.out.println("------------------------------");
        System.out.println("Date\t\tType\t\tAmount\t\tDescription");
        System.out.println("------------------------------");
        for (Transaction transaction : transactions) {
            if(transaction.getDate().after(fromDate) && transaction.getDate().before(toDate))
                System.out.println(transaction.getDate() + "\t" + transaction.getType() + "\t\t" + transaction.getAmount() + "\t\t" + transaction.getDescription());
        }
        System.out.println("------------------------------");
        auditService.logAction(String.format("user_%s_generated_statement_for_acc_%s", userId, accountId));
    }
}
