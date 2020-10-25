
public class Main {

    public static void main(String[] args) {
        System.out.println(SocialDistanceMaximizer.maxDistance(new int[]{1, 0, 0, 0, 1, 0, 1}));
        System.out.println(SocialDistanceMaximizer.maxDistance(new int[]{1, 0, 0, 0}));
        System.out.println(SocialDistanceMaximizer.maxDistance(new int[]{0, 0, 0, 1}));
        System.out.println(SocialDistanceMaximizer.maxDistance(new int[]{0, 0, 0, 0, 1}));
        System.out.println(SocialDistanceMaximizer.maxDistance(new int[]{1, 0, 0, 0, 0}));
        System.out.println(SocialDistanceMaximizer.maxDistance(new int[]{1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0}));
        System.out.println(SocialDistanceMaximizer.maxDistance(new int[]{0, 1}));
        System.out.println(SocialDistanceMaximizer.maxDistance(new int[]{1, 0}));
        System.out.println(SocialDistanceMaximizer.maxDistance(new int[]{1, 0, 1, 0, 1, 0, 0 , 1}));
    }

}
