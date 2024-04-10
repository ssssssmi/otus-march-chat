package ru.smi.march.chat.server;

import java.sql.SQLException;

import static ru.smi.march.chat.server.JDBCUserService.roles;
import static ru.smi.march.chat.server.JDBCUserService.users;

public class InMemoryAuthenticationService implements AuthenticationService{

    @Override
    public boolean register(String nickname, String login, String password) {
        if (isLoginAlreadyExist(login)) {
            return false;
        }
        if (isNicknameAlreadyExist(nickname)) {
            return false;
        }
        try {
            JDBCUserService.addUserToBase(nickname, login, password);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean isLoginAlreadyExist(String login) {
        for (User u : users.values()) {
            if (u.getLogin().equals(login)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isNicknameAlreadyExist(String nickname) {
        for (String n : users.keySet()) {
            if (n.equals(nickname)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isAdmin(String nickname) {
        for (String n : users.keySet())
            for (Role r : roles) {
                if (n.equals(nickname))
                    return r.getName().equals("admin");
            }
        return false;
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        for (String n : users.keySet())
            for (User u : users.values()) {
                if (u.getLogin().equals(login) && u.getPassword().equals(password)) {
                    return n;
                }
        }
        return null;
    }
}