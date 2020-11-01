package bg.sofia.uni.fmi.mjt.revolut.card;

import java.time.LocalDate;

public class VirtualPermanentCard extends BaseCard {

    public VirtualPermanentCard(String number, int pin, LocalDate localDate) {
        super(number, pin, localDate);
    }

    @Override
    public String getType() {
        return "VIRTUALPERMANENT";
    }

}

