package dao;

import interfaces.GenericDAO;
import model.account.Account;
import model.user.Address;
import model.user.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO implements GenericDAO<User> {
    private final Connection connection;
    private AddressDAO addressDAO;
    private AccountDAO accountDAO;
    public UserDAO(Connection connection, AddressDAO addressDAO, AccountDAO accountDAO){
        this.connection = connection;
        this.addressDAO = addressDAO;
        this.accountDAO = accountDAO;
    }
    @Override
    public void create(User user) {
        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO Usr (firstName, lastName, email, passwrd, phoneNumber, dateOfBirth, addressId) VALUES (?, ?, ?, ?, ?, ?, ?)");
            this.setParameters(stmt,  user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword(), user.getPhoneNumber(), new java.sql.Date(user.getDateOfBirth().getTime()), user.getAddress().getId());
            stmt.executeUpdate();

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 1) {
                try (Statement queryStmt = connection.createStatement();
                     ResultSet rs = queryStmt.executeQuery("SELECT user_id_seq.CURRVAL FROM dual")) {
                    if (rs.next()) {
                        int generatedId = rs.getInt(1);
                        user.setId(generatedId);
                    }
                }
            }

            // insert address
            addressDAO.create(user.getAddress());
            // insert accounts
            for (Account account : user.getAccountList()) {
                account.setUserId(user.getId());
                accountDAO.create(account);
            }
        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
        }
    }

    @Override
    public User read(int id) {
        String sql = "SELECT * FROM Usr WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    Address addr = addressDAO.read(resultSet.getInt("addressId"));
                    List<Account> accList = accountDAO.selectAllWhereUserId(id);
                    return new User(
                            resultSet.getInt("id"),
                            resultSet.getString("firstName"),
                            resultSet.getString("lastName"),
                            resultSet.getString("email"),
                            resultSet.getString("phoneNumber"),
                            resultSet.getDate("dateOfBirth"),
                            addr,
                            accList); // create a User object from the retrieved data
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error reading user: " + e.getMessage());
            return null;
        }
    }
    public void update(User user) {
        String sql = "UPDATE Usr SET firstName = ?, lastName = ?, email = ?, passwrd = ?, phoneNumber = ?, dateOfBirth = ?, addressId = ? WHERE id = ?";
        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            this.setParameters(stmt, user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword(), user.getPhoneNumber(), new java.sql.Date(user.getDateOfBirth().getTime()), user.getAddress().getId());
            stmt.executeUpdate();
            // update address
            addressDAO.update(user.getAddress());
            // update accounts
            for (Account account : user.getAccountList()) {
                accountDAO.update(account);
            }
        }catch(SQLException e){
            System.err.println("Error updating user: " + e.getMessage());
        }
    }

    public void delete(int id) {
        List<Account> accounts = accountDAO.selectAllWhereUserId(id);
        // delete associated accounts
        for (Account account : accounts) {
            accountDAO.delete(account);
        }
        String sql = "DELETE FROM Usr WHERE id = ?";
        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
        catch(SQLException e){
            System.err.println("Error deleting user: " + e.getMessage());
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id FROM Usr";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
             while (rs.next()){
                 int id = rs.getInt(1);
                 users.add(this.read(id));
             }
        }
        catch(SQLException e){
            System.err.println("Error fetching users: " + e.getMessage());
        }
        return users;
    }

}
