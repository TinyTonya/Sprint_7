import client.ScooterServiceClient;
import config.TestConfig;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import model.Courier;
import model.Credentials;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;

public class LoginCourierTest {
    private Courier courier;
    private ScooterServiceClient client;

    @Before
    public void before() {

    client = new ScooterServiceClient(TestConfig.BASE_URI);
    courier = new Courier("fd5ce679-51a0-4eb1-a0de-110eca2cde64", "testPass", "testName");
    ValidatableResponse response =  client.createCourier(courier);
        Assume.assumeTrue(response.extract().statusCode() == 201);
    }

    @Test
    @DisplayName("Курьер может залогиниться")
    public void canLogin() {
        ValidatableResponse response = client.loginCourier(new Credentials(courier.getLogin(), courier.getPassword()));
        response.assertThat().statusCode(200);
        response.assertThat().body("id", notNullValue());
    }

    @Test
    @DisplayName("Логин под несуществующим логином")
    public void loginWithNonExistentUsersLogin() {
        ValidatableResponse response = client.loginCourier(new Credentials("nonExistentLogin", courier.getPassword()));
        response.assertThat().statusCode(404);
        response.assertThat().body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Логин под несуществующим паролем")
    public void loginWithNonExistentUsersPassword() {
        ValidatableResponse response = client.loginCourier(new Credentials(courier.getLogin(), "nonExistentPassword"));
        response.assertThat().statusCode(404);
        response.assertThat().body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Успешный логин курьера с обоими обязательными полями")
    public void loginCourierWithAllRequiredFields() {
        ValidatableResponse response = client.loginCourier(new Credentials(courier.getLogin(), courier.getPassword()));
        response.assertThat().statusCode(200);
        response.assertThat().body("id", notNullValue());
    }

    @Test
    @DisplayName("Логин курьера без логина невозможен")
    public void loginCourierWithoutLogin() {
        ValidatableResponse response = client.loginCourier(new Credentials(null, courier.getPassword()));
        response.assertThat().statusCode(400);
        response.assertThat().body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Логин курьера без пароля невозможен")
    public void loginCourierWithoutPassword() {
        ValidatableResponse response = client.loginCourier(new Credentials(courier.getLogin(), null));
        response.assertThat().statusCode(400);
        response.assertThat().contentType(ContentType.JSON); // указываем тип контента
        response.assertThat().body("message", equalTo("Недостаточно данных для входа"));
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
