import client.ScooterServiceClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.ListOfOrders;
import config.TestConfig;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class GetOrderTest {
    private ScooterServiceClient client;

    @Before
    public void before() {
        client = new ScooterServiceClient(TestConfig.BASE_URI);
    }
    @Test
    @DisplayName("Запрос с limit=100 и page=0 возвращает список заказов")
    public void checkIfGetOrdersResponseIsList() {
        ValidatableResponse response = client.getOrders( 100, 0);
        response.statusCode(200);
        List<ListOfOrders> orders = response.extract().jsonPath().getList("orders", ListOfOrders.class);
        assertNotNull(orders);
        assertFalse(orders.isEmpty());
    }
}