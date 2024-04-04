package service;

import model.account.Account;
import model.account.CheckingAccount;
import model.user.User;

import java.util.List;

public class UserService {
    private static List<User> users;
    public UserService(){
    }
    public UserService(List<User> users) {
        UserService.users = users;
    }

    public List<User> getUsers() {
        return users;
    }
    public boolean isUserRegistered(int userId) {
        for (User user : users) {
            if (user.getId() == userId) {
                return true;
            }
        }
        return false;
    }
    public User getUserById(int id){
        for(User user: users)
            if (user.getId() == id)
                return user;
        return null;
    }
    public void registerUser(User user, double initialBalance) {
        users.add(user);
        CheckingAccount checkingAccount = new CheckingAccount(initialBalance);
        user.addCheckingAccount(checkingAccount);
        System.out.println("User created successfully with ID: " + user.getId());
        System.out.println("Checking account opened successfully for user: " + user.getId());
    }
    public boolean login(int userId, String password) {
        boolean isRegistered = isUserRegistered(userId);

        if (!isRegistered) {
            System.out.println("You are not currently registered in the bank's database.");
            System.out.println("Please contact a bank representative to register an account and try again.");
            return false;
        }
        User currentUser = getUserById(userId);
        if (!currentUser.passwordIsSet()) {
            System.out.println("You need to activate your account.");
            return false;
        } else {
            if (!password.equals(currentUser.getPassword())) {
                System.out.println("Wrong password!");
                return false;
            } else {
                return true;
            }
        }
    }
    public boolean activateAccount(int userId){
        boolean isRegistered = isUserRegistered(userId);

        if (!isRegistered) {
            System.out.println("You are not currently registered in the bank's database.");
            System.out.println("Please contact a bank representative to register an account and try again.");
            return false;
        }
        User currentUser = getUserById(userId);
        if (currentUser.passwordIsSet()){
            System.out.println("Account is already activated.");
            return false;
        }
        currentUser.setPassword();
        return true;
    }
    public String viewAllAccounts(int userId){
        User user = getUserById(userId);
        return user.listAccounts();
    }
//    public void openNewAccount(int userId){
//        User user = getUserById(userId);
//
//    }
}
