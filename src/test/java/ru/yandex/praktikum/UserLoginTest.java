package ru.yandex.praktikum;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.hamcrest.Matchers.equalTo;
import static ru.yandex.praktikum.UrlEndpointsData.MAIN_URL;

//Класс тестов проверки эндпоинта "Автризация пользователя" /api/auth/login
public class UserLoginTest {
    private final UserClient userClient = new UserClient();
    private final UserAccountData userAccountData = new UserAccountData();
    private final String userLogin = userAccountData.getLoginJson();
    private final String userPassword = userAccountData.getPasswordJson();
    private final String userName = userAccountData.getNameJson();
    private String json;
    private String token;

    @Before
    @Description("Инициализация данных перед тестированием")
    public void setUp() {
        RestAssured.baseURI = MAIN_URL;
        token = null;
        json = "{" + userLogin + "\"," + userName + "\"," + userPassword + "\"}";
        token = userClient.createUser(json).then().extract().path("accessToken");
    }

    @After
    @Description("Очистка данных после тестирования")
    public void closeDown() {
        if (token != null) userClient.deleteUser(token.substring(7));
    }

    @Test
    @DisplayName("Проверка авторизации с правильным логином и паролем")
    @Description("Авторизация корректной учетной записи")
    public void loginUserTest() {
        json = "{" + userLogin + "\"," + userName + "\"," + userPassword + "\"}";
        userClient.loginExistingUser(json)
                .then()
                .assertThat()
                .statusCode(HTTP_OK)
                .and()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Проверка авторизации пользователя c неправильным логином и паролем")
    @Description("Авторизация некорректной учетной записи - email + password")
    public void loginUserIncorrectEmailAndPasswordTest() {
        String addPassword = RandomStringUtils.randomAlphabetic(5);
        json = "{" + userLogin.replaceFirst("@mail", "@yandex") + "\"," + userName + "\"," + userPassword + addPassword + "\"}";
        userClient.loginExistingUser(json)
                .then()
                .assertThat()
                .statusCode(HTTP_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Проверка авторизации пользователя c неправильным логином")
    @Description("Авторизация некорректной учетной записи - email")
    public void loginUserIncorrectEmailTest() {
        json = "{" + userLogin.replaceFirst("@mail", "@yandex") + "\"," + userName + "\"," + userPassword + "\"}";
        userClient.loginExistingUser(json)
                .then()
                .assertThat()
                .statusCode(HTTP_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Проверка авторизации пользователя c неправильным паролем")
    @Description("Авторизация некорректной учетной записи - password")
    public void loginUserIncorrectPasswordTest() {
        String addPassword = RandomStringUtils.randomAlphabetic(5);
        json = "{" + userLogin + "\"," + userName + "\"," + userPassword + addPassword + "\"}";
        userClient.loginExistingUser(json)
                .then()
                .assertThat()
                .statusCode(HTTP_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

}
