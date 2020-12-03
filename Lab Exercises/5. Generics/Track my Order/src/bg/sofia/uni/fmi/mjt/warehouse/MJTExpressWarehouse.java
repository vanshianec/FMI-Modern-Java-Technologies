package bg.sofia.uni.fmi.mjt.warehouse;

import bg.sofia.uni.fmi.mjt.warehouse.exceptions.CapacityExceededException;
import bg.sofia.uni.fmi.mjt.warehouse.exceptions.ParcelNotFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.time.temporal.ChronoUnit.DAYS;

public class MJTExpressWarehouse<L, P> implements DeliveryServiceWarehouse<L, P> {

    private int capacity;
    private int retentionPeriod;
    private Map<L, P> items;
    private Map<L, LocalDateTime> submissionDates;

    public MJTExpressWarehouse(int capacity, int retentionPeriod) {
        this.capacity = capacity;
        this.retentionPeriod = retentionPeriod;
        items = new HashMap<>();
        submissionDates = new HashMap<>();
    }

    @Override
    public void submitParcel(L label, P parcel, LocalDateTime submissionDate) throws CapacityExceededException {

        validateSubmitParams(label, parcel, submissionDate);

        if (isStorageFull()) {
            clearExpiredParcels();
            if (isStorageFull()) {
                throw new CapacityExceededException("The warehouse is full!");
            }
        }

        items.put(label, parcel);
        submissionDates.put(label, submissionDate);
    }

    @Override
    public P getParcel(L label) {
        if (label == null) {
            throw new IllegalArgumentException("Label should not be null!");
        }

        return items.get(label);
    }

    @Override
    public P deliverParcel(L label) throws ParcelNotFoundException {
        if (label == null) {
            throw new IllegalArgumentException("Label should not be null!");
        }

        if (!items.containsKey(label)) {
            throw new ParcelNotFoundException("There is no parcel with such label in the storage!");
        }

        submissionDates.remove(label);
        return items.remove(label);
    }

    @Override
    public double getWarehouseSpaceLeft() {
        double spaceLeft = (capacity - items.size()) * 1.0 / capacity;
        //rounds up to 2 decimal plates
        return Math.round(spaceLeft * 100.0) / 100.0;
    }

    @Override
    public Map<L, P> getWarehouseItems() {
        return items;
    }

    @Override
    public Map<L, P> deliverParcelsSubmittedBefore(LocalDateTime before) {
        if (before == null) {
            throw new IllegalArgumentException("Date should not be null!");
        }

        Map<L, P> parcelsSubmittedBeforeDate = new HashMap<>();
        for (Map.Entry<L, LocalDateTime> entry : submissionDates.entrySet()) {
            if (entry.getValue().isBefore(before)) {
                parcelsSubmittedBeforeDate.put(entry.getKey(), items.remove(entry.getKey()));
            }
        }

        for (Map.Entry<L, P> entry : parcelsSubmittedBeforeDate.entrySet()) {
            submissionDates.remove(entry.getKey());
        }

        return parcelsSubmittedBeforeDate;
    }

    @Override
    public Map<L, P> deliverParcelsSubmittedAfter(LocalDateTime after) {
        if (after == null) {
            throw new IllegalArgumentException("Date should not be null!");
        }

        Map<L, P> parcelsSubmittedAfterDate = new HashMap<>();
        for (Map.Entry<L, LocalDateTime> entry : submissionDates.entrySet()) {
            if (entry.getValue().isAfter(after)) {
                parcelsSubmittedAfterDate.put(entry.getKey(), items.remove(entry.getKey()));
            }
        }

        for (Map.Entry<L, P> entry : parcelsSubmittedAfterDate.entrySet()) {
            submissionDates.remove(entry.getKey());
        }

        return parcelsSubmittedAfterDate;
    }

    private void validateSubmitParams(L label, P parcel, LocalDateTime submissionDate) {
        if (label == null) {
            throw new IllegalArgumentException("Label should not be null!");
        }
        if (parcel == null) {
            throw new IllegalArgumentException("Parcel should not be null!");
        }
        if (submissionDate == null) {
            throw new IllegalArgumentException("Submission date should not be null!");
        }

        if (submissionDate.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("The provided date should not be a future date!");
        }
    }

    private boolean isStorageFull() {
        return items.size() >= capacity;
    }

    private void clearExpiredParcels() {
        List<L> expired = new LinkedList<>();
        for (Map.Entry<L, LocalDateTime> entry : submissionDates.entrySet()) {
            long daysPassed = DAYS.between(entry.getValue(), LocalDateTime.now());
            if (daysPassed > retentionPeriod) {
                expired.add(entry.getKey());
            }
        }

        for (L label : expired) {
            items.remove(label);
            submissionDates.remove(label);
        }
    }
}
