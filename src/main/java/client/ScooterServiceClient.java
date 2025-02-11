package client;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import model.Courier;
import io.restassured.response.ValidatableResponse;
import model.Credentials;
import model.Order;

import static io.restassured.RestAssured.given;

public class ScooterServiceClient {

    private String baseUri;
    private String courierId;
    private String track;

    public ScooterServiceClient(String baseUri) {
        this.baseUri = baseUri;
    }

    @Step("Создание курьера")
    public ValidatableResponse createCourier(Courier courier) {
        return given()
                .filter(new AllureRestAssured())
                .log()
                .all()
                .baseUri(baseUri)
                .header("Content-Type", "application/json")
                .body(courier)
                .post("api/v1/courier")
                .then()
                .log()
                .all();
    }

    @Step("Логин курьера")
    public ValidatableResponse loginCourier(Credentials credentials) {
        ValidatableResponse response = given()
                .filter(new AllureRestAssured())
                .log()
                .all()
                .baseUri(baseUri)
                .header("Content-Type", "application/json")
                .body(credentials)
                .post("api/v1/courier/login")
                .then()
                .log()
                .all();
        Integer courierId = response.extract().path("id");
        this.courierId = String.valueOf(courierId);
        return response;
    }

    @Step("Удаление курьера")
    public ValidatableResponse deleteCourier(Integer courierId) {
        return given()
                .filter(new AllureRestAssured())
                .log()
                .all()
                .baseUri(baseUri)
                .header("Content-Type", "application/json")
                .delete("api/v1/courier/" + courierId)
                .then()
                .log()
                .all();
    }

    @Step("Создание заказа")
    public ValidatableResponse createOrder(Order order) {
        ValidatableResponse response = given()
                .filter(new AllureRestAssured())
                .log()
                .all()
                .baseUri(baseUri)
                .header("Content-Type", "application/json")
                .body(order)
                .post("api/v1/orders")
                .then()
                .log()
                .all();
        Integer trackInt = response.extract().path("track");
        String track = String.valueOf(trackInt);
        this.track = track;
        return response;
    }

    @Step("Отмена заказа")
    public ValidatableResponse cancelOrder(Integer track) {
        return given()
                .filter(new AllureRestAssured())
                .log()
                .all()
                .baseUri(baseUri)
                .header("Content-Type", "application/json")
                .body("{\"track\": " + track + "}")
                .put("api/v1/orders/cancel")
                .then()
                .log()
                .all();
    }

    @Step("Получение списка заказов")
    public ValidatableResponse getOrders(Integer limit, Integer page) {
        return given()
                .filter(new AllureRestAssured())
                .log()
                .all()
                .baseUri(baseUri)
                .queryParam("limit", limit)
                .queryParam("page", page)
                .get("api/v1/orders")
                .then()
                .log()
                .all();
    }
}
