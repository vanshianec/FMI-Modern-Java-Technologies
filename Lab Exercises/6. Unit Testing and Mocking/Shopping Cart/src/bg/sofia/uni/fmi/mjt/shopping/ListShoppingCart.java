package bg.sofia.uni.fmi.mjt.shopping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import bg.sofia.uni.fmi.mjt.shopping.item.Item;

public class ListShoppingCart extends BaseShoppingCart {

    private List<Item> items;

    public ListShoppingCart(ProductCatalog catalog) {
        super(catalog);
        this.items = new ArrayList<>();
    }

    @Override
    public int getItemsCount() {
        return items.size();
    }

    @Override
    public Collection<Item> getUniqueItems() {
        return new HashSet<>(items);
    }

    @Override
    public void addItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException(BaseShoppingCart.ITEM_NOT_NULL_MESSAGE);
        }

        items.add(item);
    }

    @Override
    public void removeItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException(BaseShoppingCart.ITEM_NOT_NULL_MESSAGE);
        }

        boolean removed = items.remove(item);
        if (!removed) {
            throw new ItemNotFoundException(String.format(
                    BaseShoppingCart.ITEM_DOES_NOT_EXIST_STRING_FORMAT,
                    item.getId()));
        }
    }

    @Override
    public double getTotal() {
        double total = 0;
        ProductCatalog catalog = super.getCatalog();

        for (Item item : items) {
            ProductInfo info = catalog.getProductInfo(item.getId());
            total += info.price();
        }

        return total;
    }

    @Override
    public Collection<Item> getSortedItems() {
        Map<Item, Integer> itemToQuantity = createMap();
        List<Item> sortedItems = new ArrayList<>(itemToQuantity.keySet());
        sortedItems.sort(new Comparator<Item>() {
            @Override
            public int compare(Item item1, Item item2) {
                return itemToQuantity.get(item2).compareTo(itemToQuantity.get(item1));
            }
        });

        return sortedItems;
    }

    private Map<Item, Integer> createMap() {
        Map<Item, Integer> itemToQuantity = new HashMap<>();
        for (Item item : items) {
            boolean containsKey = itemToQuantity.containsKey(item);
            itemToQuantity.put(item, containsKey ? itemToQuantity.get(item) + 1 : 1);
        }
        return itemToQuantity;
    }
}