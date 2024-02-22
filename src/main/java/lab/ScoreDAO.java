package lab;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScoreDAO {

    public  ScoreDAO(){
        init();

        insert(new Score("Vladislav", 50));

        findAll();
    }

    public List<Score> findAll() {
        List<Score> result = new ArrayList<>();

        String sql = "SELECT name, score_value FROM score";

        try (Connection conn = getConnection()) {
            try (Statement ps = conn.createStatement()) {
                ResultSet rs = ps.executeQuery(sql);
                while (rs.next()) {
                    result.add(new Score(rs.getString("name"), rs.getInt("score_value")));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public void insert(Score score) {
        String sql = "INSERT INTO score (name, score_value) VALUES (?, ?)";

        try (Connection conn = getConnection()) {
            try {
                assert conn != null;
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, score.getNamePlayer());
                    ps.setInt(2, score.getScorePlayer());

                    ps.execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        String sqlCreateTable = "CREATE TABLE score (" +
                "id INT NOT NULL GENERATED ALWAYS AS IDENTITY," +
                "score_value INT NOT NULL," +
                "name VARCHAR(255) NOT NULL," +
                "PRIMARY KEY (id)" +
                ")";

        try (Connection conn = getConnection()) {
            try {
                assert conn != null;
                try (Statement st = conn.createStatement()) {
                    st.execute(sqlCreateTable);
                }
            } catch (SQLException e) {
                if(!e.getSQLState().equals("X0Y32")) {
                    throw e;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() {
        try {
            return DriverManager.getConnection(
                    "jdbc:derby:scoreDB;create=true"
            );
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}