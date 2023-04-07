package ru.yandex.praktikum;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.hamcrest.Matchers.equalTo;
import static ru.yandex.praktikum.UrlEndpointsData.MAIN_URL;

//Класс тестов проверки эндпоинта "Получить заказы конкретного пользователя" /api/orders
public class GetOrdersUserTest {
    private final OrderClient orderClient = new OrderClient();
    private final UserClient userClient = new UserClient();
    private final UserAccountData userAccountData = new UserAccountData();
    private final String userLogin = userAccountData.getLoginJson();
    private final String userPassword = userAccountData.getPasswordJson();
    private final String userName = userAccountData.getNameJson();
    private String token;

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
    @DisplayName("Получение заказа от авторизованного пользователя")
    @Description("Получение заказа авторизованного пользователя")
    public void getOrdersWithAuthTest() {
        OrderData ingredients = new OrderData(orderClient.getIngredientsListTest());
        orderClient.createOrder(ingredients, token.substring(7));
        orderClient.getOrdersWithAuthUser(token.substring(7))
                .then()//.log().all()
                .assertThat()
                .statusCode(HTTP_OK)
                .and()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Получение заказа пользователя без авторизации")
    @Description("Ошибка получения заказа неавторизованного пользователя")
    public void getOrdersWithoutAuthTest() {
        orderClient.getOrdersWithoutAuthUser()
                .then()
                .assertThat()
                .statusCode(HTTP_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
}


