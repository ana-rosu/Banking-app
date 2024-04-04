package model.user;

import model.account.Account;
import model.account.CheckingAccount;
import model.account.SavingsAccount;
import user.Address;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//individuals who interact with the banking application
public class User {
    private final int id;
    static int contorId = 1;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    private Date dateOfBirth;

    private Address address;

    private List<Account> accountList;

    {
        this.id = contorId++;
    }
    public User(String firstName, String lastName, String email, String password, String phoneNumber, Date dateOfBirth, Address address, List<Account> accountList) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.accountList = accountList;
    }

    public User(Scanner in) {
        System.out.println("First name: ");
        this.firstName = in.nextLine();
        System.out.println("Last name: ");
        this.lastName = in.nextLine();

//        System.out.println("Email: ");
//        do {
//            this.email = in.nextLine();
//            if (!isValidEmail(email)) {
//                System.out.println("Invalid email address. Please try again:");
//            }
//        } while (!isValidEmail(email));
//
//        System.out.println("Phone number: ");
//        do {
//            this.phoneNumber = in.nextLine();
//            if (!isPhoneNumberValid(phoneNumber)) {
//                System.out.println("Invalid phone number. Please try again:");
//            }
//        } while (!isPhoneNumberValid(phoneNumber));
//
//        System.out.println("Birth Date (yyyy-mm-dd): ");
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        dateFormat.setLenient(false);
//        boolean validInput = false;
//        do {
//            String userInput = in.nextLine();
//
//            try {
//                dateOfBirth = dateFormat.parse(userInput);
//                if (dateOfBirth.after(new Date())) {
//                    System.out.println("Birth date cannot be in the future. Please try again.");
//                } else {
//                    validInput = true;
//                }
//            } catch (ParseException e) {
//                System.out.println("Invalid date format. Please enter the date in yyyy-MM-dd format.");
//            }
//        } while (!validInput);
//
//        System.out.println("\tAddress");
        this.address = new Address(in);
        this.accountList = new ArrayList<>();
    }
    public static boolean isValidEmail(String email) {
        String regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    public static boolean isPhoneNumberValid(String phoneNumber){
        String regex = "^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }
    public int getId() {
        return id;
    }
    public void addCheckingAccount(CheckingAccount checkingAccount){
        accountList.add(checkingAccount);
    }
    public void setPassword() {
        String newPassword;
        String confirmPassword;

        Scanner in = new Scanner(System.in);
        do {
            System.out.print("Enter your password: ");
            newPassword = in.nextLine();

            System.out.print("Confirm your password: ");
            confirmPassword = in.nextLine();

            if (!newPassword.equals(confirmPassword)) {
                System.out.println("Passwords do not match. Please try again.");
            }
        } while (!newPassword.equals(confirmPassword));

        this.password = newPassword;
    }

    @Override
    public String toString() {
        return  "-------------------" +
                "\nid: " + id +
                "\nfirstName: " + firstName +
                "\nlastName: " + lastName +
                "\nemail: " + email +
                "\npassword: " + password +
                "\nphoneNumber: " + phoneNumber +
                "\ndateOfBirth: " + dateOfBirth +
                "\naddress: " + address +
                "\naccountList: " + listAccounts() +
                "\n-------------------";
    }
    public String listAccounts(){
        StringBuilder sb = new StringBuilder();
        for(Account account: accountList)
            sb.append(account);
        return String.valueOf(sb);
    }
    public List<Account> getAccountList() {
        return accountList;
    }

    public boolean passwordIsSet() {
        return password != null && !password.isEmpty();
    }

    public String getPassword() {
        return password;
    }
//    public List<Account> addAccount(String accType){
//        Account acc = null;
//        if(accType.equals("checking")){
//            acc = new CheckingAccount(0.0);
//        }
//        else{
//            acc = new SavingsAccount(0.0);
//        }
////        return accountList.add(acc);
//    }
}
