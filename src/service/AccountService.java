package service;

import model.user.User;
import utils.UserUtils;

public class AccountService {
    private static UserUtils userUtils;
    public String viewAllAccounts(int userId){
        User user = userUtils.getUserById(userId);
        return user.listAccounts();
    }
}
