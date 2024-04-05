package service;

import model.account.CheckingAccount;
import model.user.User;
import utils.UserUtils;

public class UserService {
    private static UserUtils userUtils;
    public UserService(){
    }
    public void registerUser(User user, double initialBalance) {
        userUtils.addUser(user);
        CheckingAccount checkingAccount = new CheckingAccount(initialBalance);
        user.addCheckingAccount(checkingAccount);
        System.out.println("User created successfully with ID: " + user.getId());
        System.out.println("Checking account opened successfully for user: " + user.getId());
    }
    public boolean login(int userId, String password) {
        boolean isRegistered = userUtils.isUserRegistered(userId);

        if (!isRegistered) {
            System.out.println("You are not currently registered in the bank's database.");
            System.out.println("Please contact a bank representative to register an account and try again.");
            return false;
        }
        User currentUser = userUtils.getUserById(userId);
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
        boolean isRegistered = userUtils.isUserRegistered(userId);

        if (!isRegistered) {
            System.out.println("You are not currently registered in the bank's database.");
            System.out.println("Please contact a bank representative to register an account and try again.");
            return false;
        }
        User currentUser = userUtils.getUserById(userId);
        if (currentUser.passwordIsSet()){
            System.out.println("Account is already activated.");
            return false;
        }
        currentUser.setPassword();
        return true;
    }
}
