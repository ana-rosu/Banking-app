package dao;

import interfaces.GenericDAO;
import model.card.Card;

import java.sql.*;

public class CardDAO implements GenericDAO<Card> {
    private Connection connection;
    public CardDAO(Connection connection){
        this.connection = connection;
    }
    @Override
    public void create(Card card) {
        String sql = "INSERT INTO card (cardNumber, cardHolderName, expiryDate, CVV) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            this.setParameters(stmt, card.getCardNumber(), card.getCardHolderName(), card.getExpiryDate(), card.getCVV());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 1) {
                try (Statement queryStmt = connection.createStatement();
                     ResultSet rs = queryStmt.executeQuery("SELECT card_id_seq.CURRVAL FROM dual")) {
                    if (rs.next()) {
                        int generatedId = rs.getInt(1);
                        card.setId(generatedId);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating card: " + e.getMessage());
        }
    }


    @Override
    public Card read(int id) {
        String sql = "SELECT * FROM card WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    return new Card(resultSet.getInt("id"), resultSet.getString("cardNumber"), resultSet.getString("cardHolderName"), resultSet.getDate("expiryDate"), resultSet.getInt("CVV")); // create an Card object from the retrieved data
                } else {
                    return null;  // card with the specified id was not found
                }
            }
        } catch (SQLException e) {
            System.err.println("Error reading card: " + e.getMessage());
            return null;
        }
    }

    public void update(Card card) {
        String sql = "UPDATE card SET street = ?, city = ?, county = ?, country = ? WHERE id = ?";
        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            this.setParameters(stmt, card.getCardNumber(), card.getCardHolderName(), card.getExpiryDate(), card.getCVV());
            stmt.executeUpdate();

        }catch(SQLException e){
            System.err.println("Error updating card: " + e.getMessage());
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM card WHERE id = ?";
        try(PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
        catch(SQLException e){
            System.err.println("Error deleting card: " + e.getMessage());
        }
    }
}
