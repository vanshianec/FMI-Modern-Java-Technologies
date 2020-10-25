import java.util.Arrays;

public class Remembrall {
    public static boolean isPhoneNumberForgettable(String phoneNumber) {
        if (phoneNumber == null) {
            return false;
        }

        phoneNumber = phoneNumber.replaceAll(" ", "");
        phoneNumber = phoneNumber.replaceAll("-", "");

        if (phoneNumber.equals("")) {
            return false;
        }

        char[] symbols = phoneNumber.toCharArray();
        Arrays.sort(symbols);

        if (Character.isAlphabetic(symbols[symbols.length - 1])) {
            return true;
        }

        for (int i = 0; i < symbols.length - 1; i++) {
            char current = symbols[i];
            char next = symbols[i + 1];
            if (current == next) {
                return false;
            }
        }

        return true;
    }


}
