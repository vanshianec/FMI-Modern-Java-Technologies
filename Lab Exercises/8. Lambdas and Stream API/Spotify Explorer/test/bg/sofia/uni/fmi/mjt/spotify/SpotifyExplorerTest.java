package bg.sofia.uni.fmi.mjt.spotify;

import org.junit.Before;
import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SpotifyExplorerTest {

    private static final String FIRST_LINE = "Column Names are here...." + System.lineSeparator();
    private static final String DATASET = "1,['Pesho'; 'Gosho'],pesen1,1921,55,120000,119.824,-12.506,0.196,0.579,0.697,0.346,0.13,0.07,0" + System.lineSeparator()
            + "2,['Gosho'; 'Misho'],pesen2,1971,51,120001,24.2,-4.5,0.5,0.69,0.37,0.14,0.16,0.47,0" + System.lineSeparator()
            + "3,['Az'],pesen4,1991,51,180000,24.2,-4.5,0.5,0.69,0.37,0.14,0.16,0.47,0" + System.lineSeparator()
            + "4,['Tosho'; 'Gosho'],pesen3,1981,85,119000,55.82,-1.1,0.1,0.42,0.75,0.55,0.11,0.17,0";

    private static final String EXPLICIT_TRACKS = "4,['Tosho'; 'Gosho'],pesen4,1981,85,225076,55.82,-1.0,0.6,0.42,0.75,0.55,0.11,0.17,1" + System.lineSeparator()
            + "5,['Stamat';],pesen5,1981,85,225076,55.82,-1.1,0.1,0.42,0.75,0.55,0.11,0.17,1";
    private static final String EIGHTIES_TRACKS = "1,['Tosho'; 'Gosho'],pesen4,1981,85,225076,55.82,-1.1,0.6,0.42,0.75,0.55,0.11,0.17,1" + System.lineSeparator()
            + "2,['Az'],pesen4,1980,51,415076,24.2,-4.5,0.5,0.69,0.37,0.14,0.16,0.47,0" + System.lineSeparator()
            + "3,['Tosho'; 'Gosho'],pesen3,1989,85,225076,55.82,-1.1,0.9,0.42,0.75,0.55,0.11,0.17,0";
    private static final String NINETIES_TRACKS = "1,['Tosho'; 'Gosho'],pesen4,1991,87,225076,55.82,-1.1,0.6,0.42,0.75,0.55,0.11,0.17,1" + System.lineSeparator()
            + "2,['Az'],pesen4,1990,51,415076,24.2,-4.5,0.5,0.69,0.37,0.14,0.16,0.47,0" + System.lineSeparator()
            + "3,['Tosho'; 'Gosho'],pesen3,1999,88,225076,55.82,-1.1,0.9,0.42,0.75,0.55,0.11,0.17,0";


    private SpotifyExplorer explorer;

    @Before
    public void init() {
        explorer = new SpotifyExplorer(new StringReader(FIRST_LINE + DATASET));
    }

    @Test
    public void testGetAllSpotifyTracksWithEmptyDataset() {
        Reader reader = new StringReader("");
        SpotifyExplorer explorer = new SpotifyExplorer(reader);
        assertTrue(explorer.getAllSpotifyTracks().isEmpty());
    }

    @Test
    public void testGetAllSpotifyTracksWithDataset() {
        Collection<SpotifyTrack> expectedTracks = new ArrayList<>();
        for (String line : DATASET.split(System.lineSeparator())) {
            expectedTracks.add(SpotifyTrack.of(line));
        }

        Collection<SpotifyTrack> actualTracks = explorer.getAllSpotifyTracks();

        assertEquals(expectedTracks.size(), actualTracks.size());
        assertTrue(actualTracks.containsAll(expectedTracks));
    }

    @Test
    public void testGetExplicitSpotifyTracksWithEmptyDataSet() {
        SpotifyExplorer emptyExplorer = new SpotifyExplorer(new StringReader(""));
        assertTrue(emptyExplorer.getExplicitSpotifyTracks().isEmpty());
    }

    @Test
    public void testGetExplicitSpotifyTracksWithNoExplicitElements() {
        assertTrue(explorer.getExplicitSpotifyTracks().isEmpty());
    }

    @Test
    public void testGetExplicitSpotifyTracksWithAddedExplicitElements() {
        SpotifyExplorer spotifyExplorer = new SpotifyExplorer(new StringReader(FIRST_LINE + EXPLICIT_TRACKS));
        Collection<SpotifyTrack> expectedTracks = new ArrayList<>();
        for (String line : EXPLICIT_TRACKS.split(System.lineSeparator())) {
            expectedTracks.add(SpotifyTrack.of(line));
        }

        Collection<SpotifyTrack> actualTracks = spotifyExplorer.getExplicitSpotifyTracks();

        assertEquals(expectedTracks.size(), actualTracks.size());
        assertTrue(actualTracks.containsAll(expectedTracks));
    }

    @Test
    public void testGroupSpotifyTracksByYearWithEmptyDataSet() {
        SpotifyExplorer emptyExplorer = new SpotifyExplorer(new StringReader(""));
        assertTrue(emptyExplorer.groupSpotifyTracksByYear().isEmpty());
    }

    @Test
    public void testGroupSpotifyTracksByYearWithMultipleTracksInTheSameYear() {
        Map<Integer, Set<SpotifyTrack>> expectedMap = new HashMap<>();
        for (String line : DATASET.split(System.lineSeparator())) {
            SpotifyTrack track = SpotifyTrack.of(line);
            expectedMap.putIfAbsent(track.year(), new HashSet<>());
            Set<SpotifyTrack> tracks = expectedMap.get(track.year());
            tracks.add(track);
            expectedMap.put(track.year(), tracks);
        }

        assertEquals(expectedMap, explorer.groupSpotifyTracksByYear());
    }

    @Test
    public void testGetArtistActiveYearsWithEmptyDataSet() {
        SpotifyExplorer emptyExplorer = new SpotifyExplorer(new StringReader(""));
        String artistName = "Some artist";
        assertEquals(0, emptyExplorer.getArtistActiveYears(artistName));
    }

    @Test
    public void testGetArtistActiveYearsWithNoneExistentArtist() {
        String missingArtistName = "Missing Artist";
        assertEquals(0, explorer.getArtistActiveYears(missingArtistName));
    }

    @Test
    public void testGetArtistActiveYearsWithExistentArtist() {
        String artistFromDataset = "Gosho";
        int expectedDifferenceInYears = 61;
        explorer.getArtistActiveYears(artistFromDataset);
        assertEquals(expectedDifferenceInYears, explorer.getArtistActiveYears(artistFromDataset));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTopNHighestValenceTracksFromThe80sNegativeNumber() {
        explorer.getTopNHighestValenceTracksFromThe80s(-1);
    }

    @Test
    public void testGetTopNHighestValenceTracksFromThe80sZeroNumber() {
        assertTrue(explorer.getTopNHighestValenceTracksFromThe80s(0).isEmpty());
    }

    @Test
    public void testGetTopNHighestValenceTracksFromThe80sOrder() {
        SpotifyExplorer spotifyExplorer = new SpotifyExplorer(new StringReader(FIRST_LINE + EIGHTIES_TRACKS));
        List<SpotifyTrack> expectedTracks = new ArrayList<>();
        for (String line : EIGHTIES_TRACKS.split(System.lineSeparator())) {
            expectedTracks.add(SpotifyTrack.of(line));
        }

        expectedTracks.sort((a, b) -> Double.compare(b.valence(), a.valence()));
        List<SpotifyTrack> actualTracks = spotifyExplorer.getTopNHighestValenceTracksFromThe80s(3);
        Iterator<SpotifyTrack> it = actualTracks.iterator();
        for (SpotifyTrack expectedTrack : expectedTracks) {
            //use valence since object sorting order for equal elements is unknown
            assertEquals(expectedTrack.valence(), it.next().valence(), 0.01);
        }
    }

    @Test
    public void testGetTopNHighestValenceTracksFromThe80sWithOtherTracks() {
        String expectedId = "4";
        int expectedSize = 1;
        assertEquals(1, explorer.getTopNHighestValenceTracksFromThe80s(3).size());
        assertEquals(expectedId, explorer.getTopNHighestValenceTracksFromThe80s(10).get(0).id());
        assertEquals(expectedSize, explorer.getTopNHighestValenceTracksFromThe80s(20).size());
    }

    @Test
    public void testGetTopNHighestValenceTracksFromThe80sWithSetWithout80sTracks() {
        SpotifyExplorer explorer = new SpotifyExplorer(new StringReader(FIRST_LINE + NINETIES_TRACKS));
        assertTrue(explorer.getTopNHighestValenceTracksFromThe80s(1).isEmpty());
    }

    @Test
    public void testGetTopNHighestValenceTracksFromThe80sWithEmptySet() {
        SpotifyExplorer explorer = new SpotifyExplorer(new StringReader(""));
        assertTrue(explorer.getTopNHighestValenceTracksFromThe80s(1).isEmpty());
    }

    @Test(expected = NoSuchElementException.class)
    public void testGetMostPopularTrackFromThe90sWithEmptySet() {
        SpotifyExplorer explorer = new SpotifyExplorer(new StringReader(""));
        explorer.getMostPopularTrackFromThe90s();
    }

    @Test(expected = NoSuchElementException.class)
    public void testGetMostPopularTrackFromThe90sWithSetWithout90sTracks() {
        SpotifyExplorer explorer = new SpotifyExplorer(new StringReader(FIRST_LINE + EIGHTIES_TRACKS));
        explorer.getMostPopularTrackFromThe90s();
    }

    @Test
    public void testGetMostPopularTrackFromThe90sWithOrderedSet() {
        SpotifyExplorer explorer = new SpotifyExplorer(new StringReader(FIRST_LINE + NINETIES_TRACKS));
        String expectedId = "3";
        assertEquals(expectedId, explorer.getMostPopularTrackFromThe90s().id());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNumberOfLongerTracksBeforeYearInvalidYear() {
        int randomMinute = 3;
        int invalidYear = -1;
        explorer.getNumberOfLongerTracksBeforeYear(randomMinute, invalidYear);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNumberOfLongerTracksBeforeYearInvalidMinute() {
        int invalidMinute = -1;
        int randomYear = 1990;
        explorer.getNumberOfLongerTracksBeforeYear(invalidMinute, randomYear);
    }

    @Test
    public void testGetNumberOfLongerTracksBeforeYear() {
        int randomMinute = 2;
        int randomYear = 1981;
        int expectedTrackCount = 1;
        explorer.getNumberOfLongerTracksBeforeYear(randomMinute, randomYear);
        assertEquals(expectedTrackCount, explorer.getNumberOfLongerTracksBeforeYear(randomMinute, randomYear));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTheLoudestTrackInYearNegativeYear() {
        explorer.getTheLoudestTrackInYear(-1);
    }

    @Test
    public void testGetTheLoudestTrackInYearInEmptySet() {
        SpotifyExplorer spotifyExplorer = new SpotifyExplorer(new StringReader(""));
        int randomYear = 2000;
        assertTrue(spotifyExplorer.getTheLoudestTrackInYear(randomYear).isEmpty());
    }

    @Test
    public void testGetTheLoudestTrackInYearInSetWithMissingYear() {
        int missingYear = 9999;
        assertTrue(explorer.getTheLoudestTrackInYear(missingYear).isEmpty());
    }

    @Test
    public void testGetTheLoudestTrackInYearInSetWithMultipleTracksInYear() {
       SpotifyExplorer spotifyExplorer = new SpotifyExplorer(new StringReader(FIRST_LINE + EXPLICIT_TRACKS));
       String expectedId = "4";
       assertEquals(expectedId, spotifyExplorer.getTheLoudestTrackInYear(1981).get().id());
    }
}
