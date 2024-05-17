package dao;

import interfaces.GenericDAO;
import model.user.Address;

import java.sql.*;

public class AddressDAO implements GenericDAO<Address> {
    private Connection connection;
    public AddressDAO(Connection connection){
        this.connection = connection;
    }
    @Override
    public void create(Address address) {
        String sql = "INSERT INTO address (street, city, county, country) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, address.getStreet());
            stmt.setString(2, address.getCity());
            stmt.setString(3, address.getCounty());
            stmt.setString(4, address.getCountry());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 1) {
                // manually query the database to retrieve the generated key because PreparedStatement.RETURN_GENERATED_KEYS is unsupported
                try (Statement queryStmt = connection.createStatement();
                     ResultSet rs = queryStmt.executeQuery("SELECT address_id_seq.CURRVAL FROM dual")) {
                    if (rs.next()) {
                        int generatedId = rs.getInt(1);
                        address.setId(generatedId);
                    } else {
                        throw new SQLException("No generated key was retrieved.");
                    }
                }
            } else {
                throw new SQLException("Insert failed, no rows affected.");
            }
        } catch (SQLException e) {
            System.err.println("Error creating address: " + e.getMessage());
        }
    }


    @Override
    public Address read(int id) {
        String sql = "SELECT * FROM address WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    return new Address(resultSet.getInt("id"), resultSet.getString("street"), resultSet.getString("city"), resultSet.getString("county"), resultSet.getString("country")); // create an Address object from the retrieved data
                } else {
                    return null;  // address with the specified id was not found
                }
            }
        } catch (SQLException e) {
            System.err.println("Error reading address: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void update(Address address) {
        String sql = "UPDATE address SET street = ?, city = ?, county = ?, country = ? WHERE id = ?";
        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setString(1, address.getStreet());
            stmt.setString(2, address.getCity());
            stmt.setString(3, address.getCounty());
            stmt.setString(4, address.getCountry());
            stmt.setInt(5, address.getId());
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated == 0) {
                System.out.println("Address not found! No rows updated.");
            } else {
                System.out.println("Address updated successfully.");
            }
        }catch(SQLException e){
            System.err.println("Error updating address: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM address WHERE id = ?";
        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1, id);
            int rowsDeleted = stmt.executeUpdate();

            if (rowsDeleted == 0){
                System.out.println("Address not found! No rows deleted.");
            }else{
                System.out.println("Address deleted successfully");
            }
        }
        catch(SQLException e){
            System.err.println("Error deleeting address: " + e.getMessage());
        }
    }
}
