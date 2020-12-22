package bg.sofia.uni.fmi.mjt.restaurant.customer;

import bg.sofia.uni.fmi.mjt.restaurant.MJTDiningPlace;
import bg.sofia.uni.fmi.mjt.restaurant.Restaurant;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class CustomerTest {

    private AbstractCustomer customer;
    private static final int RANDOM_CHEFS_COUNT = 3;
    private static final Restaurant RESTAURANT_INSTANCE = new MJTDiningPlace(RANDOM_CHEFS_COUNT);

    @Before
    public void init() {
        customer = new Customer(RESTAURANT_INSTANCE);
    }

    @Test
    public void testHasVipCardShouldReturnFalse() {
        assertFalse(customer.hasVipCard());
    }
}
