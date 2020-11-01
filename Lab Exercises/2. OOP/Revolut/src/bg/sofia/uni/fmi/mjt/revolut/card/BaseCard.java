package bg.sofia.uni.fmi.mjt.revolut.card;

import java.time.LocalDate;
import java.util.Objects;

public abstract class BaseCard implements Card {

    private String number;
    private int pin;
    private LocalDate expirationDate;
    private boolean blocked;
    private byte consecutiveInvalidPinsCount;

    public BaseCard(String number, int pin, LocalDate expirationDate) {
        this.number = number;
        this.pin = pin;
        this.expirationDate = expirationDate;
        blocked = false;
        consecutiveInvalidPinsCount = 0;
    }

    @Override
    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    @Override
    public boolean checkPin(int pin) {
        if (this.pin != pin) {
            consecutiveInvalidPinsCount++;
            if (consecutiveInvalidPinsCount == 3) {
                block();
            }

            return false;
        }

        return true;
    }

    @Override
    public boolean isBlocked() {
        return blocked;
    }

    @Override
    public void block() {
        blocked = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseCard baseCard = (BaseCard) o;
        return number.equals(baseCard.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }
}

