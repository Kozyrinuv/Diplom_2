package ru.yandex.praktikum;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static java.net.HttpURLConnection.*;
import static org.hamcrest.Matchers.equalTo;
import static ru.yandex.praktikum.UrlEndpointsData.MAIN_URL;

//Класс тестов проверки эндпоинта "Обновление информации о пользователе" /api/auth/user
public class UserUpdateTest {
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
    @DisplayName("Редактирование данных авторизованного пользователя поле <email>")
    @Description("Изменение поля <email> авторизованного пользователя")
    public void editEmailAuthorizationUserTest() {
        json = "{" + userLogin.replaceFirst("@mail", "@yandex") + "\"," + userName + "\"," + userPassword + "\"}";
        String email = userAccountData.getEmail().replaceFirst("@mail", "@yandex");
        String name = userAccountData.getName();
        userClient.editAuthorUser(json, token.substring(7))
                .then()
                .assertThat()
                .statusCode(HTTP_OK)
                .and()
                .body("success", equalTo(true))
                .body("user.email", equalTo(email))
                .body("user.name", equalTo(name));
    }

    @Test
    @DisplayName("Редактирование данных авторизованного пользователя поле <name>")
    @Description("Изменение поля <name> авторизованного пользователя")
    public void editNameAuthorizationUserTest() {
        String updateName = RandomStringUtils.randomAlphabetic(3);
        json = "{" + userLogin + "\"," + userName + updateName + "\"," + userPassword + "\"}";
        String email = userAccountData.getEmail();
        String name = userAccountData.getName() + updateName;
        userClient.editAuthorUser(json, token.substring(7))
                .then()
                .assertThat()
                .statusCode(HTTP_OK)
                .and()
                .body("success", equalTo(true))
                .body("user.email", equalTo(email))
                .body("user.name", equalTo(name));
    }

    @Test
    @DisplayName("Редактирование данных авторизованного пользователя поле <password>")
    @Description("Изменение поля <password> авторизованного пользователя")
    public void editPasswordAuthorizationUserTest() {
        String updatePassword = RandomStringUtils.randomAlphabetic(3);
        json = "{" + userLogin + "\"," + userName + "\"," + userPassword + updatePassword + "\"}";
        String email = userAccountData.getEmail();
        String name = userAccountData.getName();
        userClient.editAuthorUser(json, token.substring(7))
                .then()
                .assertThat()
                .statusCode(HTTP_OK)
                .and()
                .body("success", equalTo(true))
                .body("user.email", equalTo(email))
                .body("user.name", equalTo(name));
    }

    @Test
    @DisplayName("Редактирование данных НЕавторизованного пользователя <email>, <name> и <password>")
    @Description("Изменение полей <email>, <name> и <password> НЕавторизованного пользователя")
    public void editEmailNotAuthorizationUserTest() {
        String updatePassword = RandomStringUtils.randomAlphabetic(3);
        String updateName = RandomStringUtils.randomAlphabetic(3);
        json = "{" + userLogin.replaceFirst("@mail", "@yandex") + "\"," +
                userName + updateName + "\"," + userPassword + updatePassword + "\"}";
        userClient.editNoAuthorUser(json)
                .then()
                .assertThat()
                .statusCode(HTTP_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Изменение логина (поле <email>) на такой же логин как у другого авторизованного пользователя")
    @Description("Попытка изменение поля <email> на такое же как у другого авторизованного пользователя")
    public void editEmailAuthorUserSameDuplicateTest() {
        String updatePassword = RandomStringUtils.randomAlphabetic(3);
        String updateName = RandomStringUtils.randomAlphabetic(3);
        String tokenSecond ;
        String jsonSecond = "{" + userLogin.replaceFirst("@mail", "@yandex") + "\"," +
                userName + updateName + "\"," + userPassword + updatePassword + "\"}";
        tokenSecond = userClient.createUser(jsonSecond).then().extract().path("accessToken");
        json = "{" + userLogin.replaceFirst("@mail", "@yandex") + "\"," +
                userName + "\"," + userPassword + "\"}";
        userClient.editAuthorUser(json, token.substring(7))
                .then()
                .assertThat()
                .statusCode(HTTP_FORBIDDEN)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("User with such email already exists"));
        if (tokenSecond != null) userClient.deleteUser(tokenSecond.substring(7));
    }

}
