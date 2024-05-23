import dao.*;
import service.DatabaseService;
import view.ConsoleMenu;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            try(DatabaseService db = new DatabaseService()) {
                Connection connection = db.getConnection();

                AddressDAO addressDAO = new AddressDAO(connection);
                CardDAO cardDAO = new CardDAO(connection);
                TransactionDAO transactionDAO = new TransactionDAO(connection);
                AccountDAO accountDAO = new AccountDAO(connection, cardDAO, transactionDAO);
                UserDAO userDAO = new UserDAO(connection, addressDAO, accountDAO);

                ConsoleMenu menu = ConsoleMenu.getInstance(addressDAO, cardDAO, transactionDAO, accountDAO, userDAO);
                menu.run();
            } catch(SQLException e){
                System.err.println("Error with database connection: " + e.getMessage());
            }
        }catch (ClassNotFoundException e) {
            System.err.println("Error with Oracle JDBC driver: " + e.getMessage());
        }
    }
}