package service;

import dao.UserDAO;
import model.account.CheckingAccount;
import model.user.User;
import utils.AccountUtils;
import utils.UserUtils;

public class UserService {
    private UserUtils users;
    private AccountUtils accounts;
    private AuditService auditService;

    public UserService(UserUtils users, AccountUtils accounts) {
        this.users = users;
        this.accounts = accounts;
        this.auditService = AuditService.getInstance();
    }

    public void registerUser(User user, double initialBalance) {
        users.addUser(user);
        CheckingAccount checkingAccount = new CheckingAccount(initialBalance);
        user.addCheckingAccount(checkingAccount);
        accounts.addAccount(checkingAccount);
        System.out.println("User created successfully with ID: " + user.getId());
        System.out.println("Checking account opened successfully for user: " + user.getId());

//        userDAO.create(user);
        auditService.logAction(String.format("bank_registered_user_%s", user.getId()));
    }
    public boolean checkLogin(int userId){
        boolean isRegistered = users.isUserRegistered(userId);

        if (!isRegistered) {
            System.out.println("You are not currently registered in the bank's database.");
            System.out.println("Please contact a bank representative to register an account and try again.");
            return false;
        }
        User currentUser = users.getUserById(userId);
        if (!currentUser.passwordIsSet()) {
            System.out.println("You need to activate your account.");
            return false;
        }
        return true;
    }
    public boolean login(int userId, String password) {
        User currentUser = users.getUserById(userId);
        if (!password.equals(currentUser.getPassword())) {
            System.out.println("Wrong password!");
            return false;
        } else {
            auditService.logAction(String.format("user_%s_logged_in", userId));
            return true;
        }
    }
    public boolean activateAccount(int userId){
        boolean isRegistered = users.isUserRegistered(userId);

        if (!isRegistered) {
            System.out.println("You are not currently registered in the bank's database.");
            System.out.println("Please contact a bank representative to register an account and try again.");
            return false;
        }
        User currentUser = users.getUserById(userId);
        if (currentUser.passwordIsSet()){
            System.out.println("Account is already activated.");
            return false;
        }
        currentUser.setPassword();
        auditService.logAction(String.format("user_%s_activated_account", userId));
        return true;
    }
}
