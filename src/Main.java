import model.account.*;
import model.card.Card;
import model.transaction.Transaction;
import model.transaction.TransactionType;
import model.user.User;
import model.user.Address;
import service.AccountService;
import service.UserService;
import utils.AccountUtils;
import utils.TransactionUtils;
import utils.UserUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {
    private static final UserUtils userUtils = new UserUtils();
    private static final AccountUtils accountUtils = new AccountUtils();
    private static final UserService userService = new UserService(userUtils, accountUtils);
    private static final AccountService accountService = new AccountService(userUtils);

    private static void displayMainMenu() {
        System.out.println("\nMENIU");
        System.out.println("1. Bank");
        System.out.println("2. Customer");
        System.out.println("0. EXIT");
        System.out.print("Enter your choice: ");
    }
    private static void displayBankMenu(){
        System.out.println("\n-----------------------");
        System.out.println("1. Register user");
        System.out.println("2. View all users");
        System.out.println("0. BACK");
        System.out.print("Enter your choice: ");
    }
    private static void displayCustomerMenu(){
        System.out.println("\n-----------------------");
        System.out.println("1. Activate account");
        System.out.println("2. Already a member? Login");
        System.out.println("0. BACK");
        System.out.print("Enter your choice: ");
    }
    private static void displayLoggedInCustomerMenu(){
        System.out.println("\n-----------------------");
        System.out.println("1. View all your accounts");
        System.out.println("2. Open a new account");
        System.out.println("0. BACK");
        System.out.print("Enter your choice: ");
    }
    private static void displayAccountMenu(){
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
    private static void showTransactionsDisplayOptions(){
        System.out.println("1. Show only deposits");
        System.out.println("2. Show only withdrawals");
        System.out.println("3. Sort by date (ascending)");
        System.out.println("4. Sort by date (descending)");
        System.out.println("5. Sort by amount (ascending)");
        System.out.println("6. Sort by amount (descending)");
        System.out.println("7. Search by date");
        System.out.println("0. BACK");
    }
    private static void handleBankMenu(Scanner scanner) {
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
                    Map<Integer, User> users = userUtils.getUsers();
                    for (User usr : users.values()) {
                        System.out.println(usr);
                    }
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void handleCustomerMenu(Scanner scanner) {
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

    private static void handleLoggedInCustomerMenu(Scanner scanner, int loggedInUserId) {
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
                    String accounts = accountService.viewAllAccounts(loggedInUserId);
                    System.out.println(accounts);
                    int accountId = scanner.nextInt();
                    User user = userUtils.getUserById(loggedInUserId);
                    List<Account> userAccounts = user.getAccountList();
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

    private static void handleAccountMenu(Scanner scanner, int loggedInUserId, int accountId){
        int choice;
        while (true) {
            displayAccountMenu();
            choice = scanner.nextInt();
            scanner.nextLine();
            User user = userUtils.getUserById(loggedInUserId);
            Account acc = accountService.getAccount(loggedInUserId, accountId);
            Card card;
            int amount;

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
                    System.out.println("FROM (yyyy-mm-dd): ");
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    dateFormat.setLenient(false);
                    Date from = null;
                    Date to = null;
                    // todo: create a function to validate the input data to follow the DRY principle
                    boolean validInput = false;
                    do {
                        String userInput = scanner.nextLine();

                        try {
                            from = dateFormat.parse(userInput);
                            if (from.after(new Date())) {
                                System.out.println("The date cannot be in the future. Please try again.");
                            } else {
                                validInput = true;
                            }
                        } catch (ParseException e) {
                            System.out.println("Invalid date format. Please enter the date in yyyy-mm-dd format.");
                        }
                    } while (!validInput);
                    validInput = false;
                    do {
                        String userInput = scanner.nextLine();

                        try {
                            to = dateFormat.parse(userInput);
                            if (to.after(new Date())) {
                                System.out.println("The date cannot be in the future. Please try again.");
                            } else if (to.before(from)){
                                System.out.println("The end date cannot be before the start date. Please try again.");
                            }
                            else{
                                validInput = true;
                            }
                        } catch (ParseException e) {
                            System.out.println("Invalid date format. Please enter the date in yyyy-mm-dd format.");
                        }
                    } while (!validInput);

                    accountService.generateStatement(from, to, loggedInUserId, accountId);
                    break;
                case 4:
                    // Make a transfer
                    System.out.println("Enter IBAN: ");
                    String iban = scanner.nextLine();
                    System.out.println("Enter amount: ");
                    amount = scanner.nextInt();
                    Account dest = accountUtils.getAccountByIBAN(iban);
                    acc.transfer(dest, amount);
                    break;
                case 5:
                    // Make a deposit
                    System.out.println("Enter amount: ");
                    amount = scanner.nextInt();
                    acc.deposit(amount);
                    break;
                case 6:
                    // Make a withdrawal
                    System.out.println("Enter amount: ");
                    amount = scanner.nextInt();
                    acc.withdraw(amount);
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
                        acc.setLinkedCard(card);
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
                    System.out.println("Account closed successfully!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    private static void handleTransactionsMenu(Scanner scanner, List<Transaction> transactions){
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
    public static void main(String[] args) {
        Address address1 = new Address("Virgo 23", "Bragadiru", "Ilfov", "RO");
        CheckingAccount checkingAccount1 = new CheckingAccount(1000.0);
        Date startDate = new Date();
        Date endDate = new Date(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000);
        SavingsAccount savingsAccount1 = new SavingsAccount(5000.0, startDate, endDate);
        List<Account> accountList = new ArrayList<>();
        accountList.add(checkingAccount1);
        accountList.add(savingsAccount1);
        User user1 = new User("Andrei", "Popescu", "andrei.popescu@example.com", "0883132981", new Date(94, Calendar.FEBRUARY, 1), address1, accountList);
        accountUtils.addAccount(checkingAccount1);
        accountUtils.addAccount(savingsAccount1);
        userUtils.addUser(user1);

        Scanner scanner = new Scanner(System.in);
        int choice;

        while (true) {
            displayMainMenu();
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 0:
                    System.out.println("Exiting the application.");
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