package ru.smi.march.chat.server;


import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JDBCUserService {
    static final String USERS_QUERY = "SELECT * FROM user";
    private static final String DATABASE_URL = "jdbc:postgresql://localhost:5432/test";
    private static final String USER_ROLES_QUERY = """
            select r.id as id, r.name as name from user_to_role ur
            left join roles r ON r.id=ur.role_id
            where ur.user_id = ?
            """;
    private static final String ADD_USER_QUERY = "INSERT INTO user (nickname, login, password) values (?, ?, ?)";
    private static final String ADD_ROLE_TO_USER_QUERY = "INSERT INTO user_to_role (role_id) values (?) WHERE user_id=?";
    private static Connection connection;
    static HashMap<String, User> users = new HashMap<>();
    static List<Role> roles = new ArrayList<>();


    public static void connectJDBC() throws SQLException {
        connection = DriverManager.getConnection(DATABASE_URL, "postgres", "postgres");
    }

    public static void userMapper() throws SQLException {
        connectJDBC();
        try (Statement statement = connection.createStatement()) {
            try (ResultSet usersResultSet = statement.executeQuery(USERS_QUERY)) {
                while (usersResultSet.next()) {
                    int id = usersResultSet.getInt("id");
                    String nickname = usersResultSet.getString(2);
                    String login = usersResultSet.getString(3);
                    String password = usersResultSet.getString(4);
                    User user = new User(id, login, password);
                    users.put(nickname, user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void roleMapper() throws SQLException {
        connectJDBC();
        try (PreparedStatement ps = connection.prepareStatement(USER_ROLES_QUERY)) {
            for (User user : users.values()) {
                ps.setInt(1, user.getId());
                try (ResultSet usersResultSet = ps.executeQuery()) {
                    while (usersResultSet.next()) {
                        int id = usersResultSet.getInt("id");
                        String name = usersResultSet.getString("name");
                        Role role = new Role(id, name);
                        roles.add(role);
                    }
                    user.setRoles(roles);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void addUserToBase(String nickname, String login, String password) throws SQLException {
        connectJDBC();
        try (PreparedStatement ps = connection.prepareStatement(ADD_USER_QUERY)) {
            ps.setString(2, nickname);
            ps.setString(3, login);
            ps.setString(4, password);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addUserRoleToBase(String nickname, int roleID) throws SQLException {
        int userID = 0;
        for (User user : users.values()) {
            for (String n : users.keySet()) {
                if (n.equals(nickname)) {
                   userID = user.getId();
                }
            }
        }
        connectJDBC();
        try (PreparedStatement ps = connection.prepareStatement(ADD_ROLE_TO_USER_QUERY)) {
            ps.setInt(1, userID);
            ps.setInt(2, roleID);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
