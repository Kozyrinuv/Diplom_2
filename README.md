# Diplom_2 тестирование API

## Зависимости в проекте:
- Java 11;
- JUnit 4.12;
- Apache Maven 3.8.5;
- Rest Assured 4.4.0
- Allure 2.15.0


## Протестированы следующие API: 

### Создание пользователя:
- создание уникального пользователя;
- создание пользователя, который уже зарегистрирован;
- создание пользователя без заполнения одно из обязательных полей.

### Логин пользователя:
- логин под существующим пользователем,
- логин с неверным логином и паролем.

### Изменение данных пользователя:
- с авторизацией,
- без авторизации,

### Создание заказа:
- с авторизацией,
- без авторизации,
- с ингредиентами,
- без ингредиентов,
- с неверным хешем ингредиентов.

### Получение заказов конкретного пользователя:
- авторизованный пользователь,
- неавторизованный пользователь.

## Запуск тестов:
- mvn clean test

## Разработчик:
- Козырин Михаил 16_sun
