package ru.smi.march.chat.server;

import java.util.ArrayList;
import java.util.List;

public class User {
    private int id;
    private String login;
    private String password;

    private List<Role> roles = new ArrayList<>();

    public User(int id, String login, String password) {
        this.id = id;
        this.login = login;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "User{" +
               "id=" + id +
               ", name='" + login + '\'' +
               ", password='" + password + '\'' +
               ", roles=" + roles +
               '}';
    }
}
