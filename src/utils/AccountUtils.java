package utils;

import model.account.Account;
import model.user.User;

import java.util.HashMap;
import java.util.Map;

public class AccountUtils {
    private static Map<Integer, Account> accounts = new HashMap<>();

    public AccountUtils() {
    }

    public Map<Integer, Account> getAccounts() {
        return accounts;
    }
    public void addAccount(Account account) {
        accounts.put(account.getId(), account);
    }
    public Account getAccountById(int id) {
        return accounts.get(id);
    }
    public Account getAccountByIBAN(String iban) {
        for (Account acc : accounts.values()) {
            if(acc.getIBAN().equals(iban))
                return acc;
        }
        return null;
    }
}
