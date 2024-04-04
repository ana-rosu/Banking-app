
import model.account.Account;
import model.user.User;
import service.UserService;

import java.util.List;
import java.util.Scanner;


public class Main {
    private static final UserService userService = new UserService();
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
        System.out.println("0. EXIT");
        System.out.print("Enter your choice: ");
    }
    private static void displayCustomerMenu(){
        System.out.println("\n-----------------------");
        System.out.println("1. Activate account");
        System.out.println("2. Already a member? Login");
        System.out.println("0. EXIT");
        System.out.print("Enter your choice: ");
    }
    private static void displayLoggedInCustomerMenu(){
        System.out.println("\n-----------------------");
        System.out.println("1. View all your accounts");
        System.out.println("2. Open a new account");
        System.out.println("0. EXIT");
        System.out.print("Enter your choice: ");
    }
    private static void displayAccountMenu(){
        System.out.println("\n-----------------------");
        System.out.println("1. View balance");
        System.out.println("2. View transaction history");
        System.out.println("3. Generate an account statement");
        System.out.println("4. Make a transfer");
        System.out.println("5. See card details");
        System.out.println("6. Emit a debit card for this account");
        System.out.println("7. Close account");
        System.out.println("0. BACK");
        System.out.print("Enter your choice: ");
    }
    private static void showTransactionsDisplayOptions(){
        System.out.println("1. Show only deposits");
        System.out.println("2. Show only withdrawals");
        System.out.println("3. Sort by date (ascending)");
        System.out.println("4. Sort by date (descending)");
        System.out.println("5. Sort by sum (ascending)");
        System.out.println("6. Sort by sum (descending)");
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
                    List<User> users = userService.getUsers();
                    for(User u: users)
                        System.out.println(u);
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
                    System.out.println("Password: ");
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
                    String accounts = userService.viewAllAccounts(loggedInUserId);
                    System.out.println(accounts);
                    break;
                case 2:
//                    accountService.openNewAccount();
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void handleViewAllAccounts(Scanner scanner){
        int choice;
        while (true) {
            displayAccountMenu();
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 0:
                    return;
                case 1:
                    break;
                case 2:
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    public static void main(String[] args) {
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