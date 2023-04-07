package ru.yandex.praktikum;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.Matchers.equalTo;
import static ru.yandex.praktikum.UrlEndpointsData.MAIN_URL;

//Класс тестов проверки эндпоинта "Создание пользователя" /api/auth/register
public class UserCreateTest {
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
    }

    @After
    @Description("Очистка данных после тестирования")
    public void closeDown() {
        if (token != null) userClient.deleteUser(token.substring(7));
    }

    @Test
    @DisplayName("Проверка создания нового пользователя")
    @Description("Создаем нового уникального пользователя")
    public void creatingOKNewUserTest() {
        json = "{" + userLogin + "\"," + userName + "\"," + userPassword + "\"}";
        token = userClient.createUser(json)
                .then()//.log().all()
                .assertThat()
                .statusCode(HTTP_OK)
                .and()
                .body("success", equalTo(true))
                .extract().path("accessToken");
    }

    @Test
    @DisplayName("Проверка создания дубликата пользователя")
    @Description("Создаем два раза нового пользователя")
    public void creatingUserDuplicatTest() {
        json = "{" + userLogin + "\"," + userName + "\"," + userPassword + "\"}";
        token = userClient.createUser(json)
                .then()
                .assertThat()
                .statusCode(HTTP_OK)
                .and()
                .body("success", equalTo(true))
                .extract().path("accessToken");
        userClient.createUser(json)
                .then()
                .assertThat()
                .statusCode(HTTP_FORBIDDEN)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Проверка создания пользователя без поля <email>")
    @Description("Создаем нового пользователя без поля <email>")
    public void creatingUserNoEmailTest() {
        json = "{" + userName + "\"," + userPassword + "\"}";
        userClient.createUser(json)
                .then()
                .assertThat()
                .statusCode(HTTP_FORBIDDEN)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Прверка создания пользователя без поля <name>")
    @Description("Создаем нового пользователя без поля <name>")
    public void creatingUserNoNameTest() {
        json = "{" + userLogin + "\"," + userPassword + "\"}";
        userClient.createUser(json)
                .then()
                .assertThat()
                .statusCode(HTTP_FORBIDDEN)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Проверка создания пользователя без поля <password>")
    @Description("Создаем нового пользователя без поля <password>")
    public void createUserNoPasswordTest() {
        json = "{" + userLogin + "\"," + userName + "\"}";
        userClient.createUser(json)
                .then()
                .assertThat()
                .statusCode(HTTP_FORBIDDEN)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

}
