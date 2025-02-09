import client.ScooterServiceClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.Order;
import config.TestConfig;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;

    @RunWith(Parameterized.class)
    public class CreateOrderTest {
        private ScooterServiceClient client;
        private Order order;
        private List<String> colors;
        private Integer track;

        public CreateOrderTest(List<String> colors) {
            this.colors = colors;
        }

        @Before
        public void setUp() {
            client = new ScooterServiceClient(TestConfig.BASE_URI);
            order = new Order();
            order.setFirstName("testFirstName");
            order.setLastName("testLastName");
            order.setAddress("test Address, 142 apt.");
            order.setMetroStation("4");
            order.setPhone("+7 800 355 35 55");
            order.setRentTime(5);
            order.setDeliveryDate("2025-06-06");
            order.setComment("Just some test comment");
        }

        @Parameterized.Parameters
        public static Object[][] colors() {
            return new Object[][]{
                    {Arrays.asList("BLACK")},
                    {Arrays.asList("GREY")},
                    {Arrays.asList("BLACK", "GREY")},
                    {null}
            };
        }

        @Test
        @DisplayName("Создание заказа с различными цветами скутера")
        public void createOrder() {
            order.setColor(colors);
            ValidatableResponse response = client.createOrder(order);
            response.assertThat().statusCode(201);
            response.assertThat().body("track", notNullValue());
            track = response.extract().path("track");
        }

        @After
        public void after() {
            if (track != null) {
                try {
                    ValidatableResponse cancelResponse = client.cancelOrder(track);
                    cancelResponse.statusCode(200)
                            .body("ok", equalTo(true));
                } catch (AssertionError e) {
                    // Игнорировать ошибку assertion
                } catch (Throwable e) {
                    // Игнорировать другие исключения
                }
            }
        }
    }
