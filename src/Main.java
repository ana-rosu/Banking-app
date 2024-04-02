import model.user.User;

import java.text.ParseException;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) throws ParseException {
        Scanner in = new Scanner(System.in);
        User user = new User(in);
        System.out.println(user.toString());
    }
}