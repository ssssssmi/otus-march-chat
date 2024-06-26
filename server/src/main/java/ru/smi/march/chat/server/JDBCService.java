package ru.smi.march.chat.server;

public interface JDBCService {
    void addUserToBase(String nickname, String login, String password, UserRole role);

    String getNicknameIfUserInBase(String login, String password);

    UserRole getRole(String login);

    Boolean isNicknameAlreadyExist(String nickname);

    Boolean isLoginAlreadyExist(String login);

    User getUser(String login);
}