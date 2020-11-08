package bg.sofia.uni.fmi.mjt.netflix.content;

import bg.sofia.uni.fmi.mjt.netflix.content.enums.PgRating;
import bg.sofia.uni.fmi.mjt.netflix.content.enums.Genre;

public abstract class BaseStream implements Streamable {
    private String name;
    private Genre genre;
    private PgRating rating;

    protected BaseStream(String name, Genre genre, PgRating rating) {
        this.name = name;
        this.genre = genre;
        this.rating = rating;
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public PgRating getRating() {
        return rating;
    }
}