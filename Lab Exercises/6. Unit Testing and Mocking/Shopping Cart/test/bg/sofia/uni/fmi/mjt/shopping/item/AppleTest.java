package bg.sofia.uni.fmi.mjt.shopping.item;

import org.junit.Before;
import org.junit.Test;

import static bg.sofia.uni.fmi.mjt.shopping.Constants.DEFAULT_ID;
import static bg.sofia.uni.fmi.mjt.shopping.Constants.DIFFERENT_ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class AppleTest {

    private Item apple;

    @Before
    public void init() {
        apple = new Apple(DEFAULT_ID);
    }

    @Test
    public void testGetIdShouldReturnProperValue() {
        assertEquals(apple.getId(), DEFAULT_ID);
    }

    @Test
    public void testHashCodeOfApplesWithTheSameIdShouldBeIdentical() {
        Item sameApple = new Apple(DEFAULT_ID);
        assertEquals(apple.hashCode(), sameApple.hashCode());
    }

    @Test
    public void testEqualsOfApplesWithTheSameIdShouldReturnTrue() {
        Item sameApple = new Apple(DEFAULT_ID);
        assertEquals(apple, sameApple);
    }

    @Test
    public void testEqualsOfApplesWithDifferentIdsShouldReturnFalse() {
        Item differentApple = new Apple(DIFFERENT_ID);
        assertNotEquals(apple, differentApple);
    }

    @Test
    public void testEqualsWithOtherItemObject() {
        Item chocolate = new Chocolate(DEFAULT_ID);
        assertEquals(apple, chocolate);
    }

    @Test
    public void testEqualsWithNullObject() {
        assertNotEquals(apple, null);
    }
}
