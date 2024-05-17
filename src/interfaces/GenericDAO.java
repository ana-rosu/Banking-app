package interfaces;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface GenericDAO<T> {
    void create(T entity);
    T read(int id);
    default void setParameters(PreparedStatement stmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
    }
}
