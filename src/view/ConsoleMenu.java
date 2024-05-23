package view;

import dao.*;
import model.account.Account;
import model.account.AccountStatus;
import model.account.AccountType;
import model.card.Card;
import model.transaction.Transaction;
import model.transaction.TransactionType;
import model.user.User;
import service.AccountService;
import service.AuditService;
import service.UserService;
import utils.DateValidator;
import utils.TransactionUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ConsoleMenu {
    private static ConsoleMenu instance;
    private final UserService userService;
    private final AccountService accountService;
    private final AuditService auditService;
    private final AddressDAO addressDAO;
    private final CardDAO cardDAO;
    private final TransactionDAO transactionDAO;
    private final AccountDAO accountDAO;
    private final UserDAO userDAO;
    private ConsoleMenu(AddressDAO addressDAO, CardDAO cardDAO, TransactionDAO transactionDAO, AccountDAO accountDAO, UserDAO userDAO) {
        this.addressDAO = addressDAO;
        this.cardDAO = cardDAO;
        this.transactionDAO = transactionDAO;
        this.accountDAO = accountDAO;
        this.userDAO = userDAO;

        auditService = AuditService.getInstance();
        accountService = new AccountService(accountDAO, userDAO);
        userService = new UserService(userDAO, accountDAO);
    }
    public static ConsoleMenu getInstance(AddressDAO addressDAO, CardDAO cardDAO, TransactionDAO transactionDAO, AccountDAO accountDAO, UserDAO userDAO) {
        if (instance == null)
            instance = new ConsoleMenu(addressDAO, cardDAO, transactionDAO, accountDAO, userDAO);
        return instance;
    }
    private void displayMainMenu() {
        System.out.println("\nMENIU");
        System.out.println("1. Bank");
        System.out.println("2. Customer");
        System.out.println("0. EXIT");
        System.out.print("Enter your choice: ");
    }
    private void displayBankMenu(){
        System.out.println("\n-----------------------");
        System.out.println("1. Register user");
        System.out.println("2. View all users");
        System.out.println("0. BACK");
        System.out.print("Enter your choice: ");
    }
    private void displayCustomerMenu(){
        System.out.println("\n-----------------------");
        System.out.println("1. Activate account");
        System.out.println("2. Already a member? Login");
        System.out.println("0. BACK");
        System.out.print("Enter your choice: ");
    }
    private void displayLoggedInCustomerMenu(){
        System.out.println("\n-----------------------");
        System.out.println("1. View all your accounts");
        System.out.println("2. Open a new account");
        System.out.println("0. BACK");
        System.out.print("Enter your choice: ");
    }
    private void displayAccountMenu(){
        System.out.println("\n-----------------------");
        System.out.println("1. View balance");
        System.out.println("2. View transaction history");
        System.out.println("3. Generate an account statement");
        System.out.println("4. Make a transfer");
        System.out.println("5. Make a deposit");
        System.out.println("6. Make a withdrawal");
        System.out.println("7. See card details");
        System.out.println("8. Emit a debit card for this account");
        System.out.println("9. Close account");
        System.out.println("0. BACK");
        System.out.print("Enter your choice: ");
    }
    private void showTransactionsDisplayOptions(){
        System.out.println("1. Show only deposits");
        System.out.println("2. Show only withdrawals");
        System.out.println("3. Sort by date (ascending)");
        System.out.println("4. Sort by date (descending)");
        System.out.println("5. Sort by amount (ascending)");
        System.out.println("6. Sort by amount (descending)");
        System.out.println("7. Search by date");
        System.out.println("0. BACK");
    }
    private void handleBankMenu(Scanner scanner) {
        int choice;
        while (true) {
            displayBankMenu();
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 0:
                    return;
                case 1:
                    Scanner in = new Scanner(System.in);
                    User user = new User(in);
                    double initialBalance = 0.0;
                    userService.registerUser(user, initialBalance);
                    break;
                case 2:
                    Map<Integer, User> users = userService.getAllUsers();
                    if (users.isEmpty()) {
                        System.out.println("No users found.");
                        break;
                    }
                    for (User usr : users.values()) {
                        System.out.println(usr);
                    }
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void handleCustomerMenu(Scanner scanner) {
        int choice;
        while (true) {
            displayCustomerMenu();
            choice = scanner.nextInt();
            scanner.nextLine();
            int userId;

            switch (choice) {
                case 0:
                    return;
                case 1:
                    System.out.println("\n-----------------------");
                    System.out.print("Enter your id: ");
                    userId = scanner.nextInt();
                    if(!userService.activateAccount(userId)){
                        return;
                    }
                    handleLoggedInCustomerMenu(scanner, userId);
                    break;
                case 2:
                    System.out.println("\n------Enter your credentials------");
                    System.out.println("\nId: ");
                    userId = scanner.nextInt();
                    if(!userService.checkLogin(userId)){
                        return;
                    }
                    System.out.println("\nPassword: ");
                    scanner.nextLine();
                    String password = scanner.nextLine();
                    if(userService.login(userId, password)) {
                        handleLoggedInCustomerMenu(scanner, userId);
                    }
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void handleLoggedInCustomerMenu(Scanner scanner, int loggedInUserId) {
        int choice;
        while (true) {

            displayLoggedInCustomerMenu();
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 0:
                    return;
                case 1:
                    System.out.println("Please enter the id of the account you wish to manage from the list below:\n");
                    User user = userService.getUserById(loggedInUserId);
                    List<Account> userAccounts = user.getAccountList();
                    for (Account acc : userAccounts)
                        System.out.println(acc);
                    int accountId = scanner.nextInt();

                    for(Account acc : userAccounts)
                        if(accountId == acc.getId()){
                            handleAccountMenu(scanner, loggedInUserId, accountId);
                        }
                    System.out.println("Account with id provided does not exist!");
                    break;
                case 2:
                    System.out.println("\nWhat type of account would you like to open?");
                    System.out.println("\n[1] - Checking, [2] - Savings: ");
                    choice = scanner.nextInt();
                    AccountType accType;
                    if (choice == 1) {
                        accType = AccountType.CHECKING;
                    }
                    else{
                        accType = AccountType.SAVINGS;
                    }
                    accountService.openNewAccount(loggedInUserId, accType);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void handleAccountMenu(Scanner scanner, int loggedInUserId, int accountId){
        int choice;
        while (true) {
            displayAccountMenu();
            choice = scanner.nextInt();
            scanner.nextLine();
            User user = userService.getUserById(loggedInUserId);
            Account acc = accountService.getAccount(loggedInUserId, accountId);
            Card card;
            double amount;

            switch (choice) {
                case 0:
                    return;
                case 1:
                    System.out.println("BALANCE: " + acc.getBalance());
                    break;
                case 2:
                    System.out.println("---------------TRANSACTION-HISTORY---------------");
                    List<Transaction> transactions = acc.getTransactionHistory();
                    if(transactions == null) {
                        System.out.println("No transactions found!");
                        return;
                    }
                    for(Transaction t : transactions)
                        System.out.println(t);
                    handleTransactionsMenu(scanner, transactions);
                    break;
                case 3:
                    System.out.println("Enter the dates you want to generate the account statement for: ");

                    Date from = DateValidator.parseDate(scanner, "FROM (yyyy-mm-dd): ", null, new Date());
                    Date to = DateValidator.parseDate(scanner, "TO (yyyy-mm-dd): ", from, new Date());

                    accountService.generateStatement(from, to, loggedInUserId, accountId);
                    break;
                case 4:
                    // Make a transfer
                    System.out.println("Enter ID: ");
                    int id = scanner.nextInt();
                    System.out.println("Enter amount: ");
                    amount = scanner.nextDouble();
                    Account dest = accountService.getAccountById(id);
                    acc.transfer(dest, amount);
                    accountDAO.update(acc);
                    accountDAO.update(dest);
                    break;
                case 5:
                    // Make a deposit
                    System.out.println("Enter amount: ");
                    amount = scanner.nextInt();
                    acc.deposit(amount);
                    accountDAO.update(acc);
                    break;
                case 6:
                    // Make a withdrawal
                    System.out.println("Enter amount: ");
                    amount = scanner.nextInt();
                    acc.withdraw(amount);
                    accountDAO.update(acc);
                    break;
                case 7:
                    // See card details
                    card = acc.getLinkedCard();
                    if(card == null){
                        System.out.println("This account has no card associated!");
                        break;
                    }
                    System.out.println(card);
                    break;
                case 8:
                    // Emit a debit card for this account
                    if(acc.getLinkedCard() == null){
                        card = new Card(user.getFirstName() + user.getLastName());
                        cardDAO.create(card);
                        acc.setLinkedCard(card);
                        accountDAO.update(acc);
                        System.out.println("Card issued successfully!");
                    }
                    else{
                        System.out.println("This account already has a card associated!");
                    }
                    break;
                case 9:
                    // Close account
                    // todo: After 2 years delete it from the database if not reopened in the meantime.
                    acc.setAccountStatus(AccountStatus.CLOSED);
                    accountDAO.update(acc);
                    System.out.println("Account closed successfully!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    private void handleTransactionsMenu(Scanner scanner, List<Transaction> transactions){
        int choice;
        while (true) {
            showTransactionsDisplayOptions();
            choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 0:
                    return;
                case 1:
                    // Show only deposits
                    TransactionUtils.showFilteredTransactions(transactions, TransactionType.DEPOSIT);
                    break;
                case 2:
                    // Show only withdrawals
                    TransactionUtils.showFilteredTransactions(transactions, TransactionType.WITHDRAWAL);
                    break;
                case 3:
                    // Sort by date (ascending)
                    TransactionUtils.sortByDateAscending(transactions);
                    return;
                case 4:
                    // Sort by date (descending)
                    TransactionUtils.sortByDateDescending(transactions);
                    break;
                case 5:
                    // Sort by amount (ascending)
                    TransactionUtils.sortByAmountAscending(transactions);
                    break;
                case 6:
                    // Sort by amount (descending)
                    TransactionUtils.sortByAmountDescending(transactions);
                    break;
                case 7:
                    System.out.println("Please enter the date in yyyy-mm-dd format: ");
                    String dateString = scanner.nextLine();
                    TransactionUtils.searchByDate(transactions, dateString);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    public void run() {
        Scanner scanner = new Scanner(System.in);
        int choice;

        while (true) {
            displayMainMenu();
            try {
                choice = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
                scanner.nextLine();
                continue;  //restart loop
            }

            switch (choice) {
                case 0:
                    System.out.println("Exiting the application.");
                    AuditService.getInstance().close();
                    return;
                case 1:
                    handleBankMenu(scanner);
                    break;
                case 2:
                    handleCustomerMenu(scanner);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
