package ru.yandex.praktikum;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static ru.yandex.praktikum.UrlEndpointsData.*;

public class UserClient {

    @Step("Запрос на создание нового пользователя")
    public Response createUser(String json) {
        return given()
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .post(REGISTER_URL);
    }

    @Step("Запрос на удаление существующего пользователя")
    public void deleteUser(String token) {
        given()
                .contentType(ContentType.JSON)
                .auth().oauth2(token)
                .when()
                .delete(USER_URL);
    }

    @Step("запрос на авторизацию существующего пользователя")
    public Response loginExistingUser(String json) {
        return given()
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .post(LOGIN_URL);
    }

    @Step("Запрос на изменение данных авторизованного пользователя")
    public Response editAuthorUser(String json, String token) {
        return given()
                .contentType(ContentType.JSON)
                .auth().oauth2(token)
                .body(json)
                .when()
                .patch(USER_URL);
    }

    @Step("Запрос на зменение данных НЕ авторизованного пользователя")
    public Response editNoAuthorUser(String json) {
        return given()
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .patch(USER_URL);
    }

}
