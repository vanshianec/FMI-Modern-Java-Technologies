package bg.sofia.uni.fmi.mjt.shopping;

public abstract class BaseShoppingCart implements ShoppingCart {

    private ProductCatalog catalog;
    protected static final String ITEM_NOT_NULL_MESSAGE = "Item should not be null";
    protected static final String ITEM_DOES_NOT_EXIST_STRING_FORMAT = "Item with id %s does not exist";

    protected BaseShoppingCart(ProductCatalog catalog) {
        this.catalog = catalog;
    }

    public ProductCatalog getCatalog() {
        return catalog;
    }

    public abstract int getItemsCount();
}
