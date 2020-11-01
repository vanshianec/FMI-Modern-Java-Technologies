
package bg.sofia.uni.fmi.mjt.revolut.card;

import java.time.LocalDate;

public class VirtualOneTimeCard extends BaseCard {

    public VirtualOneTimeCard(String number, int pin, LocalDate localDate) {
        super(number, pin, localDate);
    }

    @Override
    public String getType() {
        return "VIRTUALONETIME";
    }

}
 