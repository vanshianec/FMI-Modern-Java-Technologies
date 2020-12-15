package bg.sofia.uni.fmi.mjt.tagger;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TaggerTest {

    private static final String CITIES_LIST = "Sofia,Bulgaria\nPleven,Bulgaria\nLondon,England";
    private static final String EXPECTED_EMPTY_OUTPUT_TEXT = "Expected output text should be empty";
    private static final String INVALID_OUTPUT_TEXT = "Output text and tagged cities text don't match";
    private static final String INVALID_TAGS_COUNT = "Invalid tags count";
    private static final String INVALID_TAGGED_CITIES = "Invalid tagged cities output";
    private static final String INVALID_TAGGED_CITIES_COUNT = "Invalid tagged cities count";

    private Tagger tagger;

    @Before
    public void init() {
        tagger = new Tagger(new StringReader(CITIES_LIST));
    }

    @Test
    public void testTagCitiesOutputWithEmptyText() {
        String emptyText = "";
        Reader reader = new StringReader(emptyText);
        Writer writer = new StringWriter();
        tagger.tagCities(reader, writer);
        assertEquals(EXPECTED_EMPTY_OUTPUT_TEXT, emptyText, writer.toString());
    }

    @Test
    public void testTagCitiesOutputWithNoTaggedCities() {
        String noTaggedCities = "some tagged cities";
        Reader reader = new StringReader(noTaggedCities);
        Writer writer = new StringWriter();
        tagger.tagCities(reader, writer);
        assertEquals(INVALID_OUTPUT_TEXT, noTaggedCities, writer.toString());
    }

    @Test
    public void testTagCitiesOutputWithSingleCity() {
        String noTaggedCities = "fdsf   3PLEven, fds";
        Reader reader = new StringReader(noTaggedCities);
        Writer writer = new StringWriter();
        tagger.tagCities(reader, writer);
        String expectedTaggedCity = "fdsf   3<city country=\"Bulgaria\">Pleven</city>, fds";
        assertEquals(INVALID_OUTPUT_TEXT, expectedTaggedCity, writer.toString());
    }

    @Test
    public void testGetAllTagsCountWithNoGetTagsInvocations() {
        assertEquals(INVALID_TAGS_COUNT, 0, tagger.getAllTagsCount());
    }

    @Test
    public void testGetAllTagsCountWithNoCitiesContainedInTagCities() {
        String textWithNoCities = "I live in Burgas and my brother lives in Liverpool"
                + "\n but they are not contained in the list";

        Reader textReader = new StringReader(textWithNoCities);
        Writer output = new StringWriter();
        tagger.tagCities(textReader, output);
        assertEquals(INVALID_TAGS_COUNT, 0, tagger.getAllTagsCount());
    }

    @Test
    public void testGetAllTagsCountWithCitiesContainedInTagCities() {
        String textWithCities = "I live in Pleven and my brother lives in London"
                + "\n and his brother lives in Sofia.";

        Reader textReader = new StringReader(textWithCities);
        Writer output = new StringWriter();
        tagger.tagCities(textReader, output);
        assertEquals(INVALID_TAGS_COUNT, 3, tagger.getAllTagsCount());
    }

    @Test
    public void testGetAllTaggedCitiesWithNoTagCitiesInvocation() {
        assertEquals(INVALID_TAGGED_CITIES, tagger.getAllTaggedCities().size(), 0);
    }

    @Test
    public void testGetAllTaggedCitiesWithTagCitiesInvocation() {
        String text = "PlEvEn soFia LoNDon";
        Reader textReader = new StringReader(text);
        Writer output = new StringWriter();
        tagger.tagCities(textReader, output);
        Collection<String> expectedTaggedCities = new ArrayList<>();
        expectedTaggedCities.add("Pleven");
        expectedTaggedCities.add("Sofia");
        expectedTaggedCities.add("London");
        Collection<String> actualTaggedCities = tagger.getAllTaggedCities();

        assertEquals(INVALID_TAGGED_CITIES_COUNT, actualTaggedCities.size(), expectedTaggedCities.size());
        assertTrue(INVALID_TAGGED_CITIES, actualTaggedCities.containsAll(expectedTaggedCities));
    }

    @Test
    public void testGetAllTaggedCitiesWithTagCitiesInvocationAndDuplicates() {
        String text = "PlEvEn soFia LoNDon Pleven-Sofia,London,London,Sofia.";
        Reader textReader = new StringReader(text);
        Writer output = new StringWriter();
        tagger.tagCities(textReader, output);
        Collection<String> expectedTaggedCities = new ArrayList<>();
        expectedTaggedCities.add("Pleven");
        expectedTaggedCities.add("Sofia");
        expectedTaggedCities.add("London");
        Collection<String> actualTaggedCities = tagger.getAllTaggedCities();

        assertEquals(INVALID_TAGGED_CITIES_COUNT, actualTaggedCities.size(), expectedTaggedCities.size());
        assertTrue(INVALID_TAGGED_CITIES, actualTaggedCities.containsAll(expectedTaggedCities));
    }

    @Test
    public void testGetNMostTaggedCitiesWithNoTagCitiesInvocation() {
        assertEquals(INVALID_TAGGED_CITIES, tagger.getNMostTaggedCities(1).size(), 0);
    }

    @Test
    public void testGetNMostTaggedCitiesWithTagCitiesInvocation() {
        String text = "PlEvEn soFia LoNDon Pleven-Sofia,London,London,Sofia,Sofia";
        Reader textReader = new StringReader(text);
        Writer output = new StringWriter();
        tagger.tagCities(textReader, output);
        Collection<String> expectedTaggedCities = new ArrayList<>();
        expectedTaggedCities.add("Sofia");
        expectedTaggedCities.add("London");
        expectedTaggedCities.add("Pleven");

        Iterator<String> it = tagger.getNMostTaggedCities(3).iterator();
        for (String expectedTaggedCity : expectedTaggedCities) {
            assertEquals(INVALID_TAGGED_CITIES, expectedTaggedCity, it.next());
        }
    }

    @Test
    public void testGetNMostTaggedCitiesEdgeCases() {
        String text = "PlEvEn soFia LoNDon Pleven-Sofia,London,London,Sofia,Sofia";
        Reader textReader = new StringReader(text);
        Writer output = new StringWriter();
        tagger.tagCities(textReader, output);
        Collection<String> expectedTaggedCities = new ArrayList<>();
        expectedTaggedCities.add("Sofia");
        expectedTaggedCities.add("London");
        expectedTaggedCities.add("Pleven");

        assertEquals(INVALID_TAGGED_CITIES, tagger.getNMostTaggedCities(0).size(), 0);
        Iterator<String> expectedIt = expectedTaggedCities.iterator();
        Iterator<String> actualIt = tagger.getNMostTaggedCities(1).iterator();
        assertEquals(INVALID_TAGGED_CITIES, expectedIt.next(), actualIt.next());
        actualIt = tagger.getNMostTaggedCities(10).iterator();
        for (String expectedTaggedCity : expectedTaggedCities) {
            assertEquals(INVALID_TAGGED_CITIES, expectedTaggedCity, actualIt.next());
        }
    }

    @Test(expected = RuntimeException.class)
    public void testTaggerConstructorIOException() throws IOException {
        Reader reader = new StringReader("");
        //close stream so tagger can throw an exception
        reader.close();
        new Tagger(reader);
    }
}
