package bg.sofia.uni.fmi.mjt.revolut.card;

import java.time.LocalDate;

public class PhysicalCard extends BaseCard {

    public PhysicalCard(String number, int pin, LocalDate localDate) {
        super(number, pin, localDate);
    }

    @Override
    public String getType() {
        return "PHYSICAL";
    }
}
