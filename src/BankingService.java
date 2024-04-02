import model.user.User;

import java.text.ParseException;
import java.util.Scanner;

public class BankingService {
    static Scanner in = new Scanner(System.in);
    private static void userStartOptions(){
        System.out.println("\t\tWelcome to our banking app!");
        System.out.println("1. Activate account");
        System.out.println("2. Already a member? Login");
        System.out.println("0. EXIT");
    }
    private static void bankStartOptions(){
        System.out.println("1. Register a new customer");
        System.out.println("2. View all customers");
        System.out.println("3. EXIT");
    }

    private static void user1Options(){
        System.out.println("Enter your ID: ");
        int id = in.nextInt();
    }
    private static void user2Options(){

    }

}
