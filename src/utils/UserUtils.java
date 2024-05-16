package utils;

import model.user.User;

import java.util.HashMap;
import java.util.Map;

public class UserUtils {
    private static Map<Integer, User> users = new HashMap<>();

    public UserUtils() {
    }
    public Map<Integer, User> getUsers() {
        return users;
    }
    public void addUser(User user) {
        users.put(user.getId(), user);
    }
    public User getUserById(int id) {
        return users.get(id);
    }
    public boolean isUserRegistered(int id) {
        return users.containsKey(id);
    }
}
