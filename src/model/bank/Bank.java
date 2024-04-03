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

    public Bank(List<User> users) {
        this.users = users;
    }

    public List<User> getUsers() {
        return users;
    }
}
