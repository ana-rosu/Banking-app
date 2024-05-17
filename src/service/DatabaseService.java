package service;

import dao.AccountDAO;
import dao.TransactionDAO;
import dao.UserDAO;
import interfaces.GenericDAO;
import model.account.Account;
import model.card.Card;
import model.transaction.Transaction;
import model.user.User;

import java.sql.*;

public class DatabaseService {
    private static final String URL = "jdbc:oracle:thin:@localhost:1521:xe";
    private static final String USERNAME = "c##anar";
    private static final String PASSWORD = "anar";

    private Connection connection;

    public DatabaseService() throws SQLException {
        connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
