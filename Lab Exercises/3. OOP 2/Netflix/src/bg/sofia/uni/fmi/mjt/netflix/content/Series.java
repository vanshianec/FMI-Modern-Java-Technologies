package bg.sofia.uni.fmi.mjt.netflix.content;

import bg.sofia.uni.fmi.mjt.netflix.content.enums.PgRating;
import bg.sofia.uni.fmi.mjt.netflix.content.enums.Genre;

public class Series extends BaseStream {

    private Episode[] episodes;

    public Series(String name, Genre genre, PgRating rating, Episode[] episodes) {
        super(name, genre, rating);
        this.episodes = episodes;
    }

    @Override
    public int getDuration() {
        int duration = 0;
        for (Episode e : episodes) {
            duration += e.getDuration();
        }

        return duration;
    }
}