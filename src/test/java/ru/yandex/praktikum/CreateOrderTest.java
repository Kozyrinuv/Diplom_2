package ru.yandex.praktikum;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static java.net.HttpURLConnection.*;
import static org.hamcrest.Matchers.equalTo;
import static ru.yandex.praktikum.UrlEndpointsData.MAIN_URL;

//Класс тестов проверки эндпоинта "Создание заказа" /api/orders
public class CreateOrderTest {
    private final OrderClient orderClient = new OrderClient();
    private final UserClient userClient = new UserClient();
    private final UserAccountData userAccountData = new UserAccountData();
    private final String userLogin = userAccountData.getLoginJson();
    private final String userPassword = userAccountData.getPasswordJson();
    private final String userName = userAccountData.getNameJson();
    private String token;
    private OrderData ingredients;

    @Before
    @Description("Инициализация данных перед тестированием")
    public void setUp() {
        RestAssured.baseURI = MAIN_URL;
        token = null;
        String json = "{" + userLogin + "\"," + userName + "\"," + userPassword + "\"}";
        token = userClient.createUser(json).then().extract().path("accessToken");
    }

    @After
    @Description("Очистка данных после тестирования")
    public void closeDown() {
        if (token != null) userClient.deleteUser(token.substring(7));
    }

    @Test
    @DisplayName("Заказ от авторизованного пользователя с валидными инградиентами")
    @Description("Заказ с существующими ингредиентами от авторизованного пользователя")
    public void createOrderWithValidIngredientsAndAuthTest() {
        ingredients = new OrderData(orderClient.getIngredientsListTest());
        orderClient.createOrder(ingredients, token.substring(7))
                .then()
                .assertThat()
                .statusCode(HTTP_OK)
                .and()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Заказ от НЕавторизованного пользователя с валидными инградиентами")
    @Description("Заказ с существующими ингредиентами от НЕавторизованного пользователя")
    public void createOrderWithoutAuthTest() {
        ingredients = new OrderData(orderClient.getIngredientsListTest());
        orderClient.createOrderWithoutAuth(ingredients)
                .then()
                .assertThat()
                .statusCode(HTTP_OK)
                .and()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Заказ без ингредиентов")
    @Description("Заказ без ингридиентов от авторизованного пользователя")
    public void createOrderWithoutIngredientsTest() {
        orderClient.createOrderWithAuthAndWithoutIngredients(token.substring(7))
                .then()
                .assertThat()
                .statusCode(HTTP_BAD_REQUEST)
                .and()
                .body("success", equalTo(false))
                .and()
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Заказ с неверным хэшем ингредиентов")
    @Description("Заказ с невалидными ингридиентами от авторизованного пользователя")
    public void createOrderWithIncorrectIngredientsTest() {
        ingredients = new OrderData(orderClient.getIngredientsIncorrectListTest());
        orderClient.createOrderWithIncorrectIngredients(ingredients, token.substring(7))
                .then()
                .assertThat()
                .statusCode(HTTP_INTERNAL_ERROR);
    }

}
