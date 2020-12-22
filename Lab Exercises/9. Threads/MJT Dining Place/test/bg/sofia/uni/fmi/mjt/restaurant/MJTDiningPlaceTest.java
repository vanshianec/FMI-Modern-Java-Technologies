package bg.sofia.uni.fmi.mjt.restaurant;

import bg.sofia.uni.fmi.mjt.restaurant.customer.AbstractCustomer;
import bg.sofia.uni.fmi.mjt.restaurant.customer.Customer;
import bg.sofia.uni.fmi.mjt.restaurant.customer.VipCustomer;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MJTDiningPlaceTest {

    private Restaurant restaurant;
    private static final int DEFAULT_CHEFS_COUNT = 10;

    @Before
    public void init() {
        restaurant = new MJTDiningPlace(DEFAULT_CHEFS_COUNT);
    }

    @Test
    public void testGetOrderCount() throws InterruptedException {
        AbstractCustomer customerThread;

        for (int i = 0; i < 5; i++) {
            customerThread = new VipCustomer(restaurant);
            customerThread.start();
        }

        for (int i = 0; i < 10; i++) {
            customerThread = new Customer(restaurant);
            customerThread.start();
        }

        Thread.sleep(1000);
        restaurant.close();

        int expectedOrders = 15;
        int restaurantOrders = restaurant.getOrdersCount();

        assertEquals(expectedOrders, restaurantOrders);
    }


}
