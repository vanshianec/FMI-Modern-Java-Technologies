package bg.sofia.uni.fmi.mjt.warehouse.exceptions;

public class CapacityExceededException extends Exception {
    public CapacityExceededException(String message) {
        super(message);
    }
}
