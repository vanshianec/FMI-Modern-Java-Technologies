package bg.sofia.uni.fmi.mjt.netflix.account;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class Account {

    private String username;
    private LocalDateTime birthdayDate;
    private int watchTime;

    public Account(String username, LocalDateTime birthdayDate) {
        this.username = username;
        this.birthdayDate = birthdayDate;
        watchTime = 0;
    }

    public String getUsername() {
        return username;
    }

    public int getAge() {
        return (int) ChronoUnit.YEARS.between(birthdayDate, LocalDateTime.now());
    }

    public void addWatchTime(int duration) {
        watchTime += duration;
    }

    public int getWatchTime() {
        return watchTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return username.equals(account.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
