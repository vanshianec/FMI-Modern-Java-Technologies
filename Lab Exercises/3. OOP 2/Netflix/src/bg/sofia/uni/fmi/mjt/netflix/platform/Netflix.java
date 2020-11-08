package bg.sofia.uni.fmi.mjt.netflix.platform;

import bg.sofia.uni.fmi.mjt.netflix.account.Account;
import bg.sofia.uni.fmi.mjt.netflix.content.Streamable;
import bg.sofia.uni.fmi.mjt.netflix.content.enums.PgRating;
import bg.sofia.uni.fmi.mjt.netflix.exceptions.ContentNotFoundException;
import bg.sofia.uni.fmi.mjt.netflix.exceptions.ContentUnavailableException;
import bg.sofia.uni.fmi.mjt.netflix.exceptions.UserNotFoundException;

import java.util.HashMap;
import java.util.Map;

public class Netflix implements StreamingService {

    private Account[] accounts;
    private Streamable[] streamableContent;
    private HashMap<Streamable, Integer> watchCount;

    public Netflix(Account[] accounts, Streamable[] streamableContent) {
        this.accounts = accounts;
        this.streamableContent = streamableContent;
        watchCount = new HashMap<>();

    }

    @Override
    public void watch(Account user, String videoContentName) throws ContentUnavailableException {
        Account account = getUser(user);
        if (account == null) {
            throw new UserNotFoundException("There is no user " + user.getUsername() + " registered in the platform");
        }
        Streamable content = findByName(videoContentName);
        if (content == null) {
            throw new ContentNotFoundException("There is no content " + videoContentName + " found in the platform");
        }

        PgRating rating = content.getRating();
        int age = account.getAge();
        if (rating == PgRating.PG13 && age < 13 || rating == PgRating.NC17 && age < 17) {
            throw new ContentUnavailableException("This content is age restricted");
        }

        account.addWatchTime(content.getDuration());
        watchCount.putIfAbsent(content, 0);
        watchCount.put(content, watchCount.get(content) + 1);
    }

    @Override
    public Streamable findByName(String videoContentName) {
        for (Streamable s : streamableContent) {
            if (s.getTitle().equals(videoContentName)) {
                return s;
            }
        }

        return null;
    }

    @Override
    public Streamable mostViewed() {
        Streamable mostViewed = null;
        int mostViewedCount = 0;
        for (Map.Entry<Streamable, Integer> entry : watchCount.entrySet()) {
            if (entry.getValue() > mostViewedCount) {
                mostViewedCount = entry.getValue();
                mostViewed = entry.getKey();
            }
        }

        return mostViewed;
    }

    @Override
    public int totalWatchedTimeByUsers() {
        int watchTime = 0;
        for (Account a : accounts) {
            watchTime += a.getWatchTime();
        }

        return watchTime;
    }

    private Account getUser(Account user) {
        for (Account a : accounts) {
            if (a.equals(user)) {
                return a;
            }
        }

        return null;
    }
}
