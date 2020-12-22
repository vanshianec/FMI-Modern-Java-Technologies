package bg.sofia.uni.fmi.mjt.restaurant;

public class Chef extends Thread {

    private final int id;
    private final Restaurant restaurant;
    private int totalCookedMeals;

    public Chef(int id, Restaurant restaurant) {
        this.id = id;
        this.restaurant = restaurant;
    }

    @Override
    public void run() {

        while (restaurant.nextOrder() != null) {
            totalCookedMeals++;
        }
    }

    /**
     * Returns the total number of meals that this chef has cooked.
     **/

    public int getTotalCookedMeals() {
        return totalCookedMeals;
    }

    public int getIdentifier() {
        return id;
    }
}