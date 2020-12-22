package bg.sofia.uni.fmi.mjt.restaurant;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public class MJTDiningPlace implements Restaurant {

    private Queue<Order> orders;
    private Chef[] chefs;
    private int ordersCount;
    private boolean isClosed;

    public MJTDiningPlace(int chefsCount) {
        chefs = new Chef[chefsCount];
        orders = new PriorityQueue<>(getOrdersComparator());
        initializeChefsThreads(chefsCount);
    }

    @Override
    public void submitOrder(Order order) {
        if (!isClosed) {
            synchronized (this) {
                orders.add(order);
                ordersCount++;
                this.notifyAll();
            }
        }
    }

    @Override
    public synchronized Order nextOrder() {
        while (orders.isEmpty() && !isClosed) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return orders.poll();
    }

    @Override
    public int getOrdersCount() {
        return ordersCount;
    }

    @Override
    public Chef[] getChefs() {
        return chefs;
    }

    @Override
    public void close() {
        isClosed = true;
        synchronized (this) {
            this.notifyAll();
        }
    }

    private void initializeChefsThreads(int count) {
        for (int i = 0; i < count; i++) {
            chefs[i] = new Chef(i, this);
            chefs[i].start();
        }
    }

    private Comparator<Order> getOrdersComparator() {
        Comparator<Order> comparator = (o1, o2) -> {
            boolean isVip1 = o1.customer().hasVipCard();
            boolean isVip2 = o2.customer().hasVipCard();

            if (isVip1 == isVip2) {
                return Integer.compare(o1.meal().getCookingTime(), o2.meal().getCookingTime());
            }

            return Boolean.compare(isVip1, isVip2);
        };

        return comparator.reversed();
    }
}
