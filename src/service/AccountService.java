package service;

import audit.AuditService;
import model.account.Account;
import model.account.AccountType;
import model.account.CheckingAccount;
import model.account.SavingsAccount;
import model.transaction.Transaction;
import model.user.User;
import utils.UserUtils;

import java.util.Date;
import java.util.List;

// manages all accounts of a user
public class AccountService {
    private UserUtils users;
    private AuditService auditService;

    public AccountService(UserUtils users) {
        this.users = users;
        this.auditService = AuditService.getInstance();
    }
    public Account getAccount(int userId, int accountId){
        User user = users.getUserById(userId);
        List<Account> accounts = user.getAccountList();
        for(Account acc : accounts){
            if(accountId == acc.getId()){
                return acc;
            }
        }
        return null;
    }
    public String viewAllAccounts(int userId) {
        User user = users.getUserById(userId);
        List<Account> accounts = user.getAccountList();
        StringBuilder sb = new StringBuilder();
        for (Account account : accounts) {
            if (account instanceof CheckingAccount) {
                sb.append(((CheckingAccount) account).toString());
            } else if (account instanceof SavingsAccount) {
                sb.append(((SavingsAccount) account).toString());
            } else {
                sb.append(account.toString());
            }
            sb.append("\n");
        }
        auditService.logAction(String.format("user_%s_viewed_all_accounts", userId));
        return sb.toString();
    }
    public void openNewAccount(int userId, AccountType accType){
        Account account;
        if (accType == AccountType.CHECKING) {
            account = new CheckingAccount(0);
        } else {
            Date startDate = new Date();
            Date endDate = new Date(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000);
            account = new SavingsAccount(0, startDate, endDate);
        }
        User user = users.getUserById(userId);
        List<Account> accounts = user.getAccountList();
        accounts.add(account);
        user.setAccountList(accounts);

        auditService.logAction(String.format("user_%s_opened_new_account", userId));
    }

    public void generateStatement(Date fromDate, Date toDate, int userId, int accountId) {
        Account account = getAccount(userId, accountId);
        List<Transaction> transactions = account.getTransactionHistory();
        if (transactions == null){
            System.out.println("No transactions found in this period!");
            return;
        }
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
