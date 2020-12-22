package bg.sofia.uni.fmi.mjt.restaurant;

import bg.sofia.uni.fmi.mjt.restaurant.customer.AbstractCustomer;
import bg.sofia.uni.fmi.mjt.restaurant.customer.Customer;
import bg.sofia.uni.fmi.mjt.restaurant.customer.VipCustomer;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ChefTest {

    private Restaurant restaurant;
    private static final int DEFAULT_CHEFS_COUNT = 10;

    @Before
    public void init() {
        restaurant = new MJTDiningPlace(DEFAULT_CHEFS_COUNT);
    }

    @Test
    public void testGetTotalCookedMeals() throws InterruptedException {
        AbstractCustomer customerThread;

        for (int i = 0; i < 3; i++) {
            customerThread = new VipCustomer(restaurant);
            customerThread.start();
        }

        for (int i = 0; i < 5; i++) {
            customerThread = new Customer(restaurant);
            customerThread.start();
        }

        Thread.sleep(1000);
        restaurant.close();

        int expectedOrders = 8;

        int chefOrders = 0;
        for (Chef chef : restaurant.getChefs()) {
            chefOrders += chef.getTotalCookedMeals();
        }

        assertEquals(expectedOrders, chefOrders);
    }
}
