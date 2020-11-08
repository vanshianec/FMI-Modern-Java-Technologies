package bg.sofia.uni.fmi.mjt.netflix.content;

import bg.sofia.uni.fmi.mjt.netflix.content.enums.PgRating;
import bg.sofia.uni.fmi.mjt.netflix.content.enums.Genre;

public class Movie extends BaseStream {

    private int duration;

    public Movie(String name, Genre genre, PgRating rating, int duration) {
        super(name, genre, rating);
        this.duration = duration;
    }

    @Override
    public int getDuration() {
        return duration;
    }
}