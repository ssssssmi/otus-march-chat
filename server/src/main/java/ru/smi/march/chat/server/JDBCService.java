package ru.smi.march.chat.server;

public interface JDBCService {
    void addUserToBase(String nickname, String login, String password, UserRole role);

    String checkUserInBase(String login, String password);

    Boolean isNicknameAlreadyExist(String nickname);

    Boolean isLoginAlreadyExist(String login);
}