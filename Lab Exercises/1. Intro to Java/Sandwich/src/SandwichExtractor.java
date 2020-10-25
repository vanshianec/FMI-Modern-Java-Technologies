import java.util.Arrays;

public class SandwichExtractor {

    public static String[] extractIngredients(String sandwich) {
        int firstBreadIndex = sandwich.indexOf("bread");
        int lastBreadIndex = sandwich.lastIndexOf("bread");
        if (firstBreadIndex == -1 || firstBreadIndex == lastBreadIndex) {
            return new String[0];
        }

        int breadWordLength = 5;
        String ingredients[] = sandwich.substring(firstBreadIndex + breadWordLength, lastBreadIndex).split("-");
        int olivesCount = 0;
        for (String ingredient : ingredients) {
            if (ingredient.equals("olives")) {
                olivesCount++;
            }
        }

        int index = 0;
        String[] ingredientsWithoutOlives = new String[ingredients.length - olivesCount];
        for (String ingredient : ingredients) {
            if (!ingredient.equals("olives")) {
                ingredientsWithoutOlives[index++] = ingredient;
            }
        }

        Arrays.sort(ingredientsWithoutOlives);
        return ingredientsWithoutOlives;
    }

}
