package ru.yandex.praktikum;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Locale;

public class UserAccountData {
    private final String loginJson;
    private final String passwordJson;
    private final String nameJson;
    private final String email;
    private final String name;

    public UserAccountData() {
        email = RandomStringUtils.randomAlphabetic(5).toLowerCase(Locale.ROOT) + "@mail.ru";
        name = RandomStringUtils.randomAlphabetic(8);
        loginJson = "\"email\": \"" + email;
        nameJson = "\"name\": \"" + name;
        passwordJson = "\"password\": \"" + RandomStringUtils.randomAlphabetic(8);
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordJson() {
        return passwordJson;
    }

    public String getNameJson() {
        return nameJson;
    }

    public String getLoginJson() {
        return loginJson;
    }

}
