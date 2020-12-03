package bg.sofia.uni.fmi.mjt.shopping;

import bg.sofia.uni.fmi.mjt.shopping.item.Apple;
import bg.sofia.uni.fmi.mjt.shopping.item.Chocolate;
import bg.sofia.uni.fmi.mjt.shopping.item.Item;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static bg.sofia.uni.fmi.mjt.shopping.Constants.DEFAULT_ID;
import static bg.sofia.uni.fmi.mjt.shopping.Constants.DEFAULT_PRODUCT_INFO_DESCRIPTION;
import static bg.sofia.uni.fmi.mjt.shopping.Constants.DEFAULT_PRODUCT_INFO_NAME;
import static bg.sofia.uni.fmi.mjt.shopping.Constants.DEFAULT_PRODUCT_INFO_PRICE;
import static bg.sofia.uni.fmi.mjt.shopping.Constants.DELTA;
import static bg.sofia.uni.fmi.mjt.shopping.Constants.INVALID_ITEMS_COUNT_MESSAGE;
import static bg.sofia.uni.fmi.mjt.shopping.Constants.INVALID_ITEMS_TOTAL_PRICE;
import static bg.sofia.uni.fmi.mjt.shopping.Constants.SECOND_ID;
import static bg.sofia.uni.fmi.mjt.shopping.Constants.SECOND_PRODUCT_INFO_PRICE;
import static bg.sofia.uni.fmi.mjt.shopping.Constants.THIRD_ID;
import static bg.sofia.uni.fmi.mjt.shopping.Constants.UNEXPECTED_ITEM_ORDER_MESSAGE;
import static bg.sofia.uni.fmi.mjt.shopping.Constants.UNEXPECTED_NON_EMPTY_COLLECTION;
import static bg.sofia.uni.fmi.mjt.shopping.Constants.NOT_ALL_ITEMS_ADDED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class ListShoppingCartTest {

    private BaseShoppingCart listShoppingCart;

    @Before
    public void init() {
        listShoppingCart = new ListShoppingCart(Mockito.mock(ProductCatalog.class));
    }

    @Test
    public void testGetUniqueItemsWithNoItemsAdded() {
        assertTrue(UNEXPECTED_NON_EMPTY_COLLECTION, listShoppingCart.getUniqueItems().isEmpty());
    }

    @Test
    public void testGetUniqueItemsWithNonDuplicateItems() {

        List<Item> expectedItems = new ArrayList<>();
        //generate three unique items
        for (int id = 0; id < 3; id++) {
            Item item = new Apple(id + "");
            expectedItems.add(item);
            listShoppingCart.addItem(item);
        }

        Collection<Item> actualItems = listShoppingCart.getUniqueItems();
        boolean containsAll = actualItems.size() == expectedItems.size()
                && actualItems.containsAll(expectedItems);
        assertTrue(NOT_ALL_ITEMS_ADDED, containsAll);
    }

    @Test
    public void testGetUniqueItemsWithDuplicateItems() {
        Item firstDuplicate = new Chocolate(DEFAULT_ID);
        Item secondDuplicate = new Apple(DEFAULT_ID);
        Item thirdDuplicate = new Chocolate(DEFAULT_ID);
        Item firstOtherDuplicate = new Chocolate(SECOND_ID);
        Item secondOtherDuplicate = new Chocolate(SECOND_ID);
        listShoppingCart.addItem(firstDuplicate);
        listShoppingCart.addItem(secondDuplicate);
        listShoppingCart.addItem(thirdDuplicate);
        listShoppingCart.addItem(firstOtherDuplicate);
        listShoppingCart.addItem(secondOtherDuplicate);
        Collection<Item> expectedItems = new ArrayList<>();
        expectedItems.add(firstDuplicate);
        expectedItems.add(firstOtherDuplicate);
        Collection<Item> actualItems = listShoppingCart.getUniqueItems();
        boolean containsAll = actualItems.size() == expectedItems.size()
                && actualItems.containsAll(expectedItems);
        assertTrue(NOT_ALL_ITEMS_ADDED, containsAll);
    }

    @Test
    public void testGetSortedItemsWithNoItemsAdded() {
        assertTrue(UNEXPECTED_NON_EMPTY_COLLECTION, listShoppingCart.getSortedItems().isEmpty());
    }

    @Test
    public void testGetSortedItemsUniqueness() {
        Item firstDuplicate = new Chocolate(DEFAULT_ID);
        Item secondDuplicate = new Apple(DEFAULT_ID);
        Item nonDuplicateItem = new Apple(SECOND_ID);
        listShoppingCart.addItem(nonDuplicateItem);
        listShoppingCart.addItem(firstDuplicate);
        listShoppingCart.addItem(secondDuplicate);

        int expectedSize = 2;
        assertEquals(NOT_ALL_ITEMS_ADDED, expectedSize, listShoppingCart.getSortedItems().size());
    }

    @Test
    public void testGetSortedItemsOrder() {
        Item firstDuplicate = new Chocolate(DEFAULT_ID);
        Item secondDuplicate = new Apple(DEFAULT_ID);
        Item thirdDuplicate = new Chocolate(DEFAULT_ID);
        Item nonDuplicateItem = new Apple(THIRD_ID);
        Item firstOtherDuplicate = new Chocolate(SECOND_ID);
        Item secondOtherDuplicate = new Chocolate(SECOND_ID);
        listShoppingCart.addItem(nonDuplicateItem);
        listShoppingCart.addItem(firstDuplicate);
        listShoppingCart.addItem(secondDuplicate);
        listShoppingCart.addItem(thirdDuplicate);
        listShoppingCart.addItem(firstOtherDuplicate);
        listShoppingCart.addItem(secondOtherDuplicate);

        List<Item> expectedOrder = new ArrayList<>();
        expectedOrder.add(firstDuplicate);
        expectedOrder.add(firstOtherDuplicate);
        expectedOrder.add(nonDuplicateItem);

        Collection<Item> actualOrder = listShoppingCart.getSortedItems();
        Iterator<Item> itemIterator = expectedOrder.iterator();
        for (Item item : actualOrder) {
            Item expected = itemIterator.next();
            assertEquals(UNEXPECTED_ITEM_ORDER_MESSAGE, expected, item);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddItemNullElement() {
        listShoppingCart.addItem(null);
    }

    @Test
    public void testAddItemItemsCount() {
        listShoppingCart.addItem(new Apple(DEFAULT_ID));
        listShoppingCart.addItem(new Apple(SECOND_ID));
        int expectedItemsCount = 2;
        assertEquals(INVALID_ITEMS_COUNT_MESSAGE, expectedItemsCount, listShoppingCart.getItemsCount());
    }

    @Test
    public void testAddItemDuplicatesCount() {
        listShoppingCart.addItem(new Apple(DEFAULT_ID));
        listShoppingCart.addItem(new Apple(DEFAULT_ID));
        int expectedItemsCount = 2;
        assertEquals(INVALID_ITEMS_COUNT_MESSAGE, expectedItemsCount, listShoppingCart.getItemsCount());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveItemNullElement() {
        listShoppingCart.removeItem(null);
    }

    @Test(expected = ItemNotFoundException.class)
    public void testRemoveItemNonExistentElement() {
        listShoppingCart.addItem(new Apple(DEFAULT_ID));
        listShoppingCart.removeItem(new Apple(SECOND_ID));
    }

    @Test
    public void testRemoveItemItemsCount() {
        listShoppingCart.addItem(new Apple(DEFAULT_ID));
        listShoppingCart.addItem(new Chocolate(SECOND_ID));
        listShoppingCart.addItem(new Apple(THIRD_ID));
        listShoppingCart.removeItem(new Chocolate(THIRD_ID));
        int expectedItemsCount = 2;
        assertEquals(INVALID_ITEMS_COUNT_MESSAGE, expectedItemsCount, listShoppingCart.getItemsCount());
    }

    @Test
    public void testRemoveItemDuplicatesCount() {
        listShoppingCart.addItem(new Apple(DEFAULT_ID));
        listShoppingCart.addItem(new Apple(DEFAULT_ID));
        listShoppingCart.removeItem(new Apple(DEFAULT_ID));
        int expectedItemsCount = 1;
        assertEquals(INVALID_ITEMS_COUNT_MESSAGE, expectedItemsCount, listShoppingCart.getItemsCount());
    }

    @Test
    public void testGetTotalReturnValue() {
        ProductCatalog productCatalogMock = Mockito.mock(ProductCatalog.class);
        ProductInfo defaultProductInfo = new ProductInfo(DEFAULT_PRODUCT_INFO_NAME,
                DEFAULT_PRODUCT_INFO_DESCRIPTION,
                DEFAULT_PRODUCT_INFO_PRICE);

        ProductInfo secondProductInfo = new ProductInfo(DEFAULT_PRODUCT_INFO_NAME,
                DEFAULT_PRODUCT_INFO_DESCRIPTION,
                SECOND_PRODUCT_INFO_PRICE);

        when(productCatalogMock.getProductInfo(DEFAULT_ID)).thenReturn(defaultProductInfo);
        when(productCatalogMock.getProductInfo(SECOND_ID)).thenReturn(secondProductInfo);

        BaseShoppingCart shoppingCart = new ListShoppingCart(productCatalogMock);
        shoppingCart.addItem(new Apple(DEFAULT_ID));
        shoppingCart.addItem(new Chocolate(DEFAULT_ID));
        shoppingCart.addItem(new Apple(SECOND_ID));

        double expectedSum = DEFAULT_PRODUCT_INFO_PRICE * 2 + SECOND_PRODUCT_INFO_PRICE;
        double actualSum = shoppingCart.getTotal();
        assertEquals(INVALID_ITEMS_TOTAL_PRICE, expectedSum, actualSum, DELTA);
    }
}
