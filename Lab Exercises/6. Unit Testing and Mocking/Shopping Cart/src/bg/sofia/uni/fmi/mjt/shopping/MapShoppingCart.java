package bg.sofia.uni.fmi.mjt.shopping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bg.sofia.uni.fmi.mjt.shopping.item.Item;

public class MapShoppingCart extends BaseShoppingCart {

    private Map<Item, Integer> items;

    public MapShoppingCart(ProductCatalog catalog) {
        super(catalog);
        items = new HashMap<>();
    }

    @Override
    public int getItemsCount() {
        int size = 0;
        for (Integer itemCount : items.values()) {
            size += itemCount;
        }
        return size;
    }

    public Collection<Item> getUniqueItems() {
        return items.keySet();
    }

    @Override
    public void addItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException(BaseShoppingCart.ITEM_NOT_NULL_MESSAGE);
        }

        if (!items.containsKey(item)) {
            items.put(item, 0);
        }
        items.put(item, items.get(item) + 1);
    }

    @Override
    public void removeItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException(BaseShoppingCart.ITEM_NOT_NULL_MESSAGE);
        }

        if (!items.containsKey(item)) {
            throw new ItemNotFoundException(String.format(
                    BaseShoppingCart.ITEM_DOES_NOT_EXIST_STRING_FORMAT,
                    item.getId()));
        }

        int occurrences = items.get(item);
        if (occurrences == 1) {
            items.remove(item);
        } else {
            items.put(item, occurrences - 1);
        }
    }

    @Override
    public double getTotal() {
        double total = 0;
        ProductCatalog catalog = super.getCatalog();

        for (Item item : items.keySet()) {
            ProductInfo info = catalog.getProductInfo(item.getId());
            total += info.price() * items.get(item);
        }

        return total;
    }

    @Override
    public Collection<Item> getSortedItems() {
        List<Item> sortedItems = new ArrayList<>(items.keySet());
        sortedItems.sort(new Comparator<Item>() {
            @Override
            public int compare(Item item1, Item item2) {
                return items.get(item2).compareTo(items.get(item1));
            }
        });

        return sortedItems;
    }

}