package dao;

import interfaces.GenericDAO;
import model.account.Account;
import model.card.Card;
import model.user.Address;
import model.user.User;

import java.sql.*;
import java.util.List;

public class UserDAO implements GenericDAO<User> {
    private Connection connection;
    public UserDAO(Connection connection){
        this.connection = connection;
    }
    @Override
    public void create(User user) {
        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO usr (firstName, lastName, email, passwrd, phoneNumber, dateOfBirth, addressId) VALUES (?, ?, ?, ?, ?, ?, ?)");
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
        } catch (SQLException e) {
            System.err.println("Error creating user: " + e.getMessage());
        }
    }

    @Override
    public User read(int id) {
        String sql = "SELECT * FROM usr WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    Address addr = getAddress(resultSet.getInt("addressId"));
                    List<Account> accList = getAccountsForUser(id);
                    return new User(resultSet.getInt("id"), resultSet.getString("firstName"), resultSet.getString("lastName"), resultSet.getString("email"), resultSet.getString("phoneNumber"), resultSet.getDate("dateOfBirth"), addr, accList); // create an Card object from the retrieved data
                } else {
                    return null;  // card with the specified id was not found
                }
            }
        } catch (SQLException e) {
            System.err.println("Error reading card: " + e.getMessage());
            return null;
        }
    }
    private Address getAddress(int addressId) throws SQLException {
        AddressDAO addressDAO = new AddressDAO(connection);
        return addressDAO.read(addressId);
    }

    private List<Account> getAccountsForUser(int userId) throws SQLException {
        AccountDAO accountDAO = new AccountDAO(connection);
        return accountDAO.selectAllWhereUserId(userId);
    }
    public void update(User user) {
        String sql = "UPDATE usr SET firstName = ?, lastName = ?, email = ?, passwrd = ?, phoneNumber = ?, dateOfBirth = ?, addressId = ? WHERE id = ?";
        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            this.setParameters(stmt, user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword(), user.getPhoneNumber(),new java.sql.Date(user.getDateOfBirth().getTime()), user.getAddress().getId());
            stmt.executeUpdate();
        }catch(SQLException e){
            System.err.println("Error updating user: " + e.getMessage());
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM usr WHERE id = ?";
        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
        catch(SQLException e){
            System.err.println("Error deleting user: " + e.getMessage());
        }
    }

}
