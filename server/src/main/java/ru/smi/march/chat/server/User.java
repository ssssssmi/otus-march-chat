package ru.smi.march.chat.server;

public class User {
    private int id;
    private String nickname;
    private String login;
    private String role;

    public User(int id, String nickname, String login, String role) {
        this.id = id;
        this.nickname = nickname;
        this.login = login;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
