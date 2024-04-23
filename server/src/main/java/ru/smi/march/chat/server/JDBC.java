package ru.smi.march.chat.server;

import java.sql.*;

public class JDBC implements JDBCService {
    private static final String DATABASE_URL = "jdbc:postgresql://localhost:5432/test";

    private static final String ADD_USER_QUERY = "INSERT INTO user (nickname, login, password, role) VALUES (?, ?, ?, ?)";
    private static final String USERS_QUERY = "SELECT id, nickname, role FROM user WHERE login = ?";
    private static final String CHECK_USER_QUERY = "SELECT nickname, login, password FROM user WHERE login = ? AND password = ?";
    private static final String EXIST_NICKNAME_QUERY = "SELECT count(*) FROM user WHERE nickname = ?";
    private static final String EXIST_LOGIN_QUERY = "SELECT count(*) FROM user WHERE login = ?";

    private static Connection connection;

    public static void connectJDBC() throws SQLException {
        connection = DriverManager.getConnection(DATABASE_URL, "postgres", "123");
    }

    @Override
    public void addUserToBase(String nickname, String login, String password, UserRole role) {
        try (PreparedStatement ps = connection.prepareStatement(ADD_USER_QUERY)) {
            ps.setString(1, nickname);
            ps.setString(2, login);
            ps.setString(3, password);
            ps.setString(4, String.valueOf(role));
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public User getUser(String login) {
        try (PreparedStatement ps = connection.prepareStatement(USERS_QUERY)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String nickname = rs.getString("nickname");
                    String role = rs.getString("role");
                    return new User(id, nickname, login, role);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getNicknameIfUserInBase(String login, String password) {
        try (PreparedStatement ps = connection.prepareStatement(CHECK_USER_QUERY)) {
            ps.setString(1, login);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(2);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Boolean isNicknameAlreadyExist(String nickname) {
        try (PreparedStatement ps = connection.prepareStatement(EXIST_NICKNAME_QUERY)) {
            ps.setString(1, nickname);
            try (ResultSet rs = ps.executeQuery()) {
                int count = rs.next() ? rs.getInt(1) : 0;
                if (count == 1) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Boolean isLoginAlreadyExist(String login) {
        try (PreparedStatement ps = connection.prepareStatement(EXIST_LOGIN_QUERY)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                int count = rs.next() ? rs.getInt(1) : 0;
                if (count == 1) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}