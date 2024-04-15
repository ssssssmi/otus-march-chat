package ru.smi.march.chat.server;

import java.sql.*;

public class JDBC implements JDBCService {
    private static final String DATABASE_URL = "jdbc:postgresql://localhost:5432/test";

    private static final String ADD_USER_QUERY = "insert into user (nickname, login, password, role) values (?, ?, ?, ?)";
    private static final String CHECK_USER_QUERY = "select nickname, login, password from user where login = ? and password = ?";
    private static final String EXIST_NICKNAME_QUERY = "select nickname from user where nickname = ?";
    private static final String EXIST_LOGIN_QUERY = "select login from user where login = ?";

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
    public String checkUserInBase(String login, String password) {
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
                if (rs.next()) {
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
                if (rs.next()) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}