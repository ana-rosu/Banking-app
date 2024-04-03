
import model.bank.Bank;
import model.user.User;
import service.UserService;

import java.util.List;
import java.util.Scanner;


public class Main {
    private static final Bank bank = new Bank();
    private static final UserService userService = new UserService(bank.getUsers());
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

            switch (choice) {
                case 0:
                    return;
                case 1:
                    System.out.println("\n-----------------------");
                    System.out.print("Enter your id: ");
                    int userId = scanner.nextInt();
                    if(!userService.activateAccount(userId)){
                        return;
                    }
                    handleAccountMenu(scanner);
                    break;
                case 2:
                    System.out.println("\n------Enter your credentials------");
                    System.out.println("\nId: ");
                    userId = scanner.nextInt();
                    System.out.println("Password: ");
                    scanner.nextLine();
                    String password = scanner.nextLine();
                    if(userService.login(userId, password)) {
                        handleAccountMenu(scanner);
                    }
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void handleAccountMenu(Scanner scanner) {
        int choice;
        while (true) {
            System.out.println("\n-----------------------");
            System.out.println("1. View all your accounts");
            System.out.println("2. Open a new account");
            System.out.println("0. EXIT");
            System.out.print("Enter your choice: ");

            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 0:
                    return;
                case 1:
//                    accountService.viewAllAccounts();
                    break;
                case 2:
//                    accountService.openNewAccount();
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