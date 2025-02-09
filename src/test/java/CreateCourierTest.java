
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.Courier;
import model.Credentials;
import config.TestConfig;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import client.ScooterServiceClient;
import static org.hamcrest.Matchers.equalTo;

public class CreateCourierTest {
    private Courier courier;
    private ScooterServiceClient client;

    @Before
    public void before() {
        client = new ScooterServiceClient(TestConfig.BASE_URI);
        courier = new Courier("f2beed9c-17f3-4df1-af45-ac73deeafe35", "testPass", "testName");
    }

    @Test
    @DisplayName("Создание нового курьера")
    public void canCreateCourier() {
        ValidatableResponse response = client.createCourier(courier);
        response.assertThat().statusCode(201).body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    public void cannotCreateTwoIdenticalCouriers() {
        // Создаем первого курьера
        ValidatableResponse response1 = client.createCourier(courier);
        response1.assertThat().statusCode(201);

        // Создаем второго курьера с теми же данными
        ValidatableResponse response2 = client.createCourier(courier);
        response2.assertThat().statusCode(409); // Ожидаем ошибку 409 Conflict
        response2.assertThat().body("message", equalTo("Этот логин уже используется")); // Проверяем тело ответа
    }

    @Test
    @DisplayName("Создание курьера с обоими обязательными полями")
    public void createCourierWithAllRequiredFields() {
        Courier courierWithoutFirstName = new Courier(courier.getLogin(), courier.getPassword(), null);
        ValidatableResponse response = client.createCourier(courierWithoutFirstName);
        response.assertThat().statusCode(201);
    }

    @Test
    @DisplayName("Создание курьера без логина")
    public void createCourierWithoutLogin() {
        Courier courierWithoutLogin = new Courier(null, courier.getPassword(), courier.getFirstName());
        ValidatableResponse response = client.createCourier(courierWithoutLogin);
        response.assertThat().statusCode(400); // Ожидаем ошибку 400 Bad Request
        response.assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи")); // Проверяем тело ответа
    }

    @Test
    @DisplayName("Создание курьера без пароля")
    public void createCourierWithoutPassword() {
        Courier courierWithoutPassword = new Courier(courier.getLogin(), null, courier.getFirstName());
        ValidatableResponse response = client.createCourier(courierWithoutPassword);
        response.assertThat().statusCode(400); // Ожидаем ошибку 400 Bad Request
        response.assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи")); // Проверяем тело ответа
    }

    @After
    public void after() {
        Credentials credentials = new Credentials(courier.getLogin(), courier.getPassword());
        ValidatableResponse loginResponse = client.loginCourier(credentials);
        Integer courierId = loginResponse.extract().path("id");
        if (courierId != null) {
            client.deleteCourier(courierId);
        }
    }
}
