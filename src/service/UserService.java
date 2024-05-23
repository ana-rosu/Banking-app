package service;

import dao.AccountDAO;
import dao.UserDAO;
import model.account.CheckingAccount;
import model.user.User;

import java.util.Map;

public class UserService {
    private AuditService auditService;
    private UserDAO userDAO;
    private AccountDAO accountDAO;

    public UserService(UserDAO userDAO, AccountDAO accountDAO) {
        this.userDAO = userDAO;
        this.accountDAO = accountDAO;
        this.auditService = AuditService.getInstance();
    }
    public User getUserById(int userId){
        return userDAO.read(userId);
    }
    public Map<Integer, User> getAllUsers(){
        return userDAO.getAllUsers();
    }
    public void registerUser(User user, double initialBalance) {
        // automatically open a checking account when registering a new user
        CheckingAccount checkingAccount = new CheckingAccount(initialBalance);
        user.addCheckingAccount(checkingAccount);
        accountDAO.create(checkingAccount);
        userDAO.create(user);

        System.out.println("User created successfully with ID: " + user.getId());
        System.out.println("Checking account opened successfully for user: " + user.getId());
        auditService.logAction(String.format("bank_registered_user_%s", user.getId()));
    }
    public boolean checkLogin(int userId){
        boolean isRegistered = userDAO.isUserRegistered(userId);

        if (!isRegistered) {
            System.out.println("You are not currently registered in the bank's database.");
            System.out.println("Please contact a bank representative to register an account and try again.");
            return false;
        }
        User currentUser = userDAO.read(userId);
        if (!currentUser.passwordIsSet()) {
            System.out.println("You need to activate your account.");
            return false;
        }
        return true;
    }
    public boolean login(int userId, String password) {
        User currentUser = userDAO.read(userId);
        if (!password.equals(currentUser.getPassword())) {
            System.out.println("Wrong password!");
            return false;
        } else {
            auditService.logAction(String.format("user_%s_logged_in", userId));
            return true;
        }
    }
    public boolean activateAccount(int userId){
        boolean isRegistered = userDAO.isUserRegistered(userId);

        if (!isRegistered) {
            System.out.println("You are not currently registered in the bank's database.");
            System.out.println("Please contact a bank representative to register an account and try again.");
            return false;
        }
        User currentUser = userDAO.read(userId);
        if (currentUser.passwordIsSet()){
            System.out.println("Account is already activated.");
            return false;
        }
        currentUser.setPassword();
        userDAO.update(currentUser);
        auditService.logAction(String.format("user_%s_activated_account", userId));
        return true;
    }
}
