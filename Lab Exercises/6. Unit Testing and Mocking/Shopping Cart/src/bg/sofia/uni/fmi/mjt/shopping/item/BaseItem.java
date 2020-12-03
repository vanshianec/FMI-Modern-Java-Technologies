package bg.sofia.uni.fmi.mjt.shopping.item;

import java.util.Objects;

public abstract class BaseItem implements Item {

    private String id;

    protected BaseItem(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BaseItem baseItem)) {
            return false;
        }

        return Objects.equals(id, baseItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
