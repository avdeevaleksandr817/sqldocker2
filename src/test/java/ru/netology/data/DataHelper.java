package ru.netology.data;

import lombok.Data;
import lombok.Value;

public class DataHelper {
    //конструктор без параметров
    private DataHelper() {
    }

    //валидный пользователь
    public static UserData getUser() {
        return new UserData("vasya", "qwerty123");
    }

    @Value
    public static class UserData {
        private final String login;
        private final String password;
    }

    //валидный код
    public static VerifyCode getValidCode(String login) {
        return new VerifyCode(login, SQLHelper.getVerifyCodeByLogin(login));
    }

    @Value
    public static class VerifyCode {
        private final String login;
        private final String code;
    }

    @Data
    public static class CardData {
        private final String id;
        private String number;
        private final String balance;
    }

    @Value
    public static class TransferData {
        private final String from;
        private final String to;
        private final String amount;
    }
}