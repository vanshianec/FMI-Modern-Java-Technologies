package bg.sofia.uni.fmi.mjt.restaurant.customer;

import bg.sofia.uni.fmi.mjt.restaurant.MJTDiningPlace;
import bg.sofia.uni.fmi.mjt.restaurant.Restaurant;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class VipCustomerTest {

    private AbstractCustomer customer;
    private static final int RANDOM_CHEFS_COUNT = 3;
    private static final Restaurant RESTAURANT_INSTANCE = new MJTDiningPlace(RANDOM_CHEFS_COUNT);

    @Before
    public void init() {
        customer = new VipCustomer(RESTAURANT_INSTANCE);
    }

    @Test
    public void testHasVipCardShouldReturnTrue() {
        assertTrue(customer.hasVipCard());
    }
}
