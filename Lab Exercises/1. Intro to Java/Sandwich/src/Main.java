import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        System.out.println(Arrays.toString(SandwichExtractor.extractIngredients("asdbreadham-tomato-mayobreadblabla")));
        System.out.println(Arrays.toString(SandwichExtractor.extractIngredients("asdbreadham-olives-tomato-olives-mayobreadblabla")));
        System.out.println(Arrays.toString(SandwichExtractor.extractIngredients("asdbreadham")));
    }
}
