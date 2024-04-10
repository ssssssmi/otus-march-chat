package ru.smi.march.chat.server;

import java.sql.SQLException;

public interface AuthenticationService {
    boolean register(String nickname, String login, String password);
    boolean isAdmin(String nickname);
    boolean isLoginAlreadyExist(String login);
    boolean isNicknameAlreadyExist(String nickname);
    String getNicknameByLoginAndPassword(String login, String password);
}