package ru.yandex.praktikum;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.List;

import static io.restassured.RestAssured.given;
import static ru.yandex.praktikum.UrlEndpointsData.ORDER_URL;

public class OrderClient {
    private final List<String> ingredientsListTest = List.of("61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa73", "61c0c5a71d1f82001bdaaa70", "61c0c5a71d1f82001bdaaa77");
    private final List<String> ingredientsIncorrectListTest = List.of("61cxxxxx1d1f82001bdaaa6d", "61cxxxxx1d1f82001bdaaa73", "61cxxxxx1d1f82001bdaaa70", "61cxxxxx1d1f82001bdaaa77");

    public List<String> getIngredientsListTest() {
        return ingredientsListTest;
    }

    public List<String> getIngredientsIncorrectListTest() {
        return ingredientsIncorrectListTest;
    }

    @Step("Получение заказов от авторизованного пользователя")
    public Response getOrdersWithAuthUser(String token) {
        return given()
                .contentType(ContentType.JSON)
                .auth().oauth2(token)
                .when()
                .get(ORDER_URL);
    }

    @Step("Получение заказов от НЕавторизованного пользователя")
    public Response getOrdersWithoutAuthUser() {
        return given()
                .contentType(ContentType.JSON)
                .when()
                .get(ORDER_URL);
    }

    @Step("Создание заказа c авторизацией и c существующими ингредиентами")
    public Response createOrder(OrderData orderData, String token) {
        return given()
                .contentType(ContentType.JSON)
                .auth().oauth2(token)
                .body(orderData)
                .when()
                .post(ORDER_URL);
    }

    @Step("Создание заказа без авторизации c существующими ингредиентами")
    public Response createOrderWithoutAuth(OrderData orderData) {
        return given()
                .contentType(ContentType.JSON)
                .body(orderData)
                .when()
                .post(ORDER_URL);
    }

    @Step("Создание заказа c авторизацией без ингредиентов")
    public Response createOrderWithAuthAndWithoutIngredients(String token) {
        return given()
                .contentType(ContentType.JSON)
                .auth().oauth2(token)
                .when()
                .post(ORDER_URL);
    }

    @Step("Создание заказа c авторизацией и несуществующими ингредиентами")
    public Response createOrderWithIncorrectIngredients(OrderData orderData, String token) {
        return given()
                .contentType(ContentType.JSON)
                .auth().oauth2(token)
                .body(orderData)
                .when()
                .post(ORDER_URL);
    }

}
