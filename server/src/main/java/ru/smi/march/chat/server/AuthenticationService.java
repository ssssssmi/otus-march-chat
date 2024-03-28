package ru.smi.march.chat.server;

public interface AuthenticationService {
    String getNicknameByLoginAndPassword(String login, String password);
    boolean register(String login, String password, String nickname, UserRole userRole);
    boolean isAdmin(String nickname);
    boolean isLoginAlreadyExist(String login);
    boolean isNicknameAlreadyExist(String nickname);
}