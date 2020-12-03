package bg.sofia.uni.fmi.mjt.shopping.item;

import org.junit.Before;
import org.junit.Test;

import static bg.sofia.uni.fmi.mjt.shopping.Constants.DEFAULT_ID;
import static bg.sofia.uni.fmi.mjt.shopping.Constants.DIFFERENT_ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ChocolateTest {

    private Item chocolate;

    @Before
    public void init() {
        chocolate = new Chocolate(DEFAULT_ID);
    }

    @Test
    public void testGetIdShouldReturnProperValue() {
        assertEquals(chocolate.getId(), DEFAULT_ID);
    }

    @Test
    public void testHashCodeOfChocolatesWithTheSameIdShouldBeIdentical() {
        Item sameChocolate = new Chocolate(DEFAULT_ID);
        assertEquals(chocolate.hashCode(), sameChocolate.hashCode());
    }

    @Test
    public void testEqualsOfChocolatesWithTheSameIdShouldReturnTrue() {
        Item sameChocolate = new Chocolate(DEFAULT_ID);
        assertEquals(chocolate, sameChocolate);
    }

    @Test
    public void testEqualsOfChocolatesWithDifferentIdsShouldReturnFalse() {
        Item differentChocolate = new Chocolate(DIFFERENT_ID);
        assertNotEquals(chocolate, differentChocolate);
    }

    @Test
    public void testEqualsWithOtherItemObject() {
        Item apple = new Apple(DEFAULT_ID);
        assertEquals(chocolate, apple);
    }

    @Test
    public void testEqualsWithNullObject() {
        assertNotEquals(chocolate, null);
    }
}
