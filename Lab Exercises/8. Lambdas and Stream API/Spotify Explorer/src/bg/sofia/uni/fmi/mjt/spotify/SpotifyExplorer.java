package bg.sofia.uni.fmi.mjt.spotify;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class SpotifyExplorer {

    private final Set<SpotifyTrack> tracks;

    private static final int FIRST_LINE = 1;
    private static final int ZERO = 0;
    private static final int ONE = 1;
    private static final int SECONDS_TO_MILISECONDS = 1000;
    private static final int MINUTES_TO_SECONDS = 60;
    private static final int EIGHTIES_START_YEAR = 1980;
    private static final int EIGHTIES_END_YEAR = 1989;
    private static final int NINETIES_START_YEAR = 1990;
    private static final int NINETIES_END_YEAR = 1999;
    private static final String READING_ERROR = "A problem occurred while reading";
    private static final String NEGATIVE_NUMBER_ERROR = "Number should not be negative";
    private static final String NEGATIVE_YEAR_ERROR = "Year should not be negative";
    private static final String NEGATIVE_MINUTES_ERROR = "Minutes should not be negative";

    /**
     * Loads the dataset from the given {@code dataInput} stream.
     *
     * @param dataInput java.io.Reader input stream from which the dataset can be read
     */

    public SpotifyExplorer(Reader dataInput) {
        try (var reader = new BufferedReader(dataInput)) {
            tracks = reader.lines()
                    .skip(FIRST_LINE)
                    .map(SpotifyTrack::of)
                    .collect(Collectors.toSet());

        } catch (IOException e) {
            throw new IllegalStateException(READING_ERROR, e);
        }
    }

    /**
     * @return all spotify tracks from the dataset as unmodifiable collection
     * If the dataset is empty, return an empty collection
     */

    public Collection<SpotifyTrack> getAllSpotifyTracks() {
        return Collections.unmodifiableCollection(tracks);
    }

    /**
     * @return all tracks from the spotify dataset classified as explicit as unmodifiable collection
     * If the dataset is empty or contains no tracks classified as explicit, return an empty collection
     */

    public Collection<SpotifyTrack> getExplicitSpotifyTracks() {
        return tracks.stream()
                .filter(SpotifyTrack::explicit)
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Returns all tracks in the dataset, grouped by release year. If no tracks were released in a given year
     * it should not appear as key in the map.
     *
     * @return map with year as a key and the set of spotify tracks released this year as value.
     * If the dataset is empty, return an empty collection
     */

    public Map<Integer, Set<SpotifyTrack>> groupSpotifyTracksByYear() {
        return tracks.stream()
                .collect(Collectors.groupingBy(SpotifyTrack::year, Collectors.toSet()));
    }

    /**
     * Returns the number of years between the oldest and the newest released tracks of an artist.
     * Note that tracks with multiple authors including the given artist should also be considered in the result.
     *
     * @param artist artist name
     * @return number of active years
     * If the dataset is empty or there are no tracks by the given artist in the dataset, return 0.
     */

    public int getArtistActiveYears(String artist) {

        int latestTrackYear = tracks.stream()
                .filter(t -> t.artists().contains(artist))
                .mapToInt(SpotifyTrack::year)
                .max()
                .orElse(0);

        int earliestTrackYear = tracks.stream()
                .filter(t -> t.artists().contains(artist))
                .mapToInt(SpotifyTrack::year)
                .min()
                .orElse(0);

        if (latestTrackYear == ZERO) {
            return ZERO;
        }

        return latestTrackYear - earliestTrackYear + ONE;
    }

    /**
     * Returns the @n tracks with highest valence from the 80s.
     * Note that the 80s started in 1980 and lasted until 1989, inclusive.
     * Valence describes the musical positiveness conveyed by a track.
     * Tracks with high valence sound more positive (happy, cheerful, euphoric),
     * while tracks with low valence sound more negative (sad, depressed, angry).
     *
     * @param n number of tracks to return
     *          If @n exceeds the total number of tracks from the 80s, return all tracks available from this period.
     * @return unmodifiable list of tracks sorted by valence in descending order
     * @throws IllegalArgumentException in case @n is a negative number.
     */

    public List<SpotifyTrack> getTopNHighestValenceTracksFromThe80s(int n) {
        if (n < ZERO) {
            throw new IllegalArgumentException(NEGATIVE_NUMBER_ERROR);
        }

        return tracks.stream()
                .filter(t -> t.year() >= EIGHTIES_START_YEAR && t.year() <= EIGHTIES_END_YEAR)
                .sorted((a, b) -> Double.compare(b.valence(), a.valence()))
                .limit(n)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Returns the most popular track from the 90s.
     * Note that the 90s started in 1990 and lasted until 1999, inclusive.
     * The value is between 0 and 100, with 100 being the most popular.
     *
     * @return the most popular track of the 90s.
     * If there more than one tracks with equal highest popularity, return any of them
     * @throws NoSuchElementException if there are no tracks from the 90s in the dataset
     */

    public SpotifyTrack getMostPopularTrackFromThe90s() {
        return tracks.stream()
                .filter(t -> t.year() >= NINETIES_START_YEAR && t.year() <= NINETIES_END_YEAR)
                .sorted((a, b) -> Integer.compare(b.popularity(), a.popularity()))
                .findAny()
                .orElseThrow(NoSuchElementException::new);
    }

    /**
     * Returns the number of tracks longer than @minutes released before @year.
     *
     * @param minutes
     * @param year
     * @return the number of tracks longer than @minutes released before @year
     * @throws IllegalArgumentException in case @minutes or @year is a negative number
     */

    public long getNumberOfLongerTracksBeforeYear(int minutes, int year) {
        if (year < ZERO) {
            throw new IllegalArgumentException(NEGATIVE_YEAR_ERROR);
        }
        if (minutes < ZERO) {
            throw new IllegalArgumentException(NEGATIVE_MINUTES_ERROR);
        }

        return tracks.stream()
                .filter(t -> t.year() < year
                        && (t.duration() > minutes * MINUTES_TO_SECONDS * SECONDS_TO_MILISECONDS))
                .count();
    }

    /**
     * Returns the loudest track released in a given year
     *
     * @param year
     * @return the loudest track released in a given year
     * @throws IllegalArgumentException in case @year is a negative number
     */

    public Optional<SpotifyTrack> getTheLoudestTrackInYear(int year) {
        if (year < ZERO) {
            throw new IllegalArgumentException(NEGATIVE_YEAR_ERROR);
        }

        return tracks.stream()
                .filter(t -> t.year() == year)
                .max(Comparator.comparingDouble(SpotifyTrack::loudness));
    }

}