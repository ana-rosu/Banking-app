package model.bank;

import model.account.CheckingAccount;
import model.user.User;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Bank {
    private List<User> users;

    public Bank() {
        this.users = new ArrayList<>();
    }
    public void addUser(double initialBalance) throws ParseException {
        Scanner in = new Scanner(System.in);
        User user = new User(in);
        users.add(user);
        CheckingAccount checkingAccount = new CheckingAccount(initialBalance);
        user.addCheckingAccount(checkingAccount);
        System.out.println("User created successfully with ID: " + user.getId());
        System.out.println("Checking account opened successfully for user: " + user.getId());
    }
}
