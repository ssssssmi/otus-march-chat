package ru.smi.march.chat.server;

import java.util.ArrayList;
import java.util.List;

public class InMemoryAuthenticationService implements AuthenticationService{
    private class User {
        private String login;
        private String password;
        private String nickname;
        private UserRole userRole;
        public User(String login, String password, String nickname, UserRole userRole) {
            this.login = login;
            this.password = password;
            this.nickname = nickname;
            this.userRole = userRole;
        }
    }
    private List<User> users;

    public InMemoryAuthenticationService() {
        this.users = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            this.users.add(new User("login" + i, "pass" + i, "nick" + i, UserRole.USER));
        }
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        for (User u : users) {
            if (u.login.equals(login) && u.password.equals(password)) {
                return u.nickname;
            }
        }
        return null;
    }

    @Override
    public boolean register(String login, String password, String nickname, UserRole userRole) {
        if (isLoginAlreadyExist(login)) {
            return false;
        }
        if (isNicknameAlreadyExist(nickname)) {
            return false;
        }
        users.add(new User(login, password, nickname, userRole));
        return true;
    }

    @Override
    public boolean isLoginAlreadyExist(String login) {
        for (User u : users) {
            if (u.login.equals(login)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isNicknameAlreadyExist(String nickname) {
        for (User u : users) {
            if (u.nickname.equals(nickname)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isAdmin(String nickname) {
        for (User u : users) {
            if (u.nickname.equals(nickname)) {
                return u.userRole == UserRole.ADMIN;
            }
        }
        return false;
    }
}