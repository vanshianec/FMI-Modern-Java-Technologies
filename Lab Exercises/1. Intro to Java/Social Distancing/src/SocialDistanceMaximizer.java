public class SocialDistanceMaximizer {
    public static int maxDistance(int[] seats) {
        int startIndex = 0, endIndex = seats.length - 1, startCount = 0, endCount = 0, middleCount = 0, middleMaxCount = 0;
        while (seats[startIndex++] != 1) {
            startCount++;
        }
        while (seats[endIndex--] != 1) {
            endCount++;
        }

        for (int i = startIndex; i <= endIndex; i++) {
            if (seats[i] == 0) {
                middleCount++;
                if (middleCount > middleMaxCount) {
                    middleMaxCount = middleCount;
                }
            } else {
                middleCount = 0;
            }
        }

        return Math.max((middleMaxCount + 1) / 2, Math.max(startCount, endCount));
    }
}
