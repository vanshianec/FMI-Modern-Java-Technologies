package bg.sofia.uni.fmi.mjt.tagger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class Tagger {

    private static final String TAG_CITIES_IO_PROBLEM_MESSAGE = "Unexpected IO problem when tagging cities";
    private static final String READ_CITIES_IO_PROBLEM_MESSAGE = "Unexpected problem with cities reader";
    private static final String REGEX_REPLACE_CASE_INSENSITIVE_CITY_FORMAT = "([^a-zA-Z]+|\\b)(?i)(%s)([^a-zA-Z]+|\\b)";
    private static final String CITY_TAG_FORMAT = "$1<city country=\"%s\">%s</city>$3";

    private Map<String, Integer> lastTaggedCities;
    private Map<String, String> citiesAndCountries;
    private long allTagsCount;

    /**
     * Creates a new instance of Tagger for a given list of city/country pairs
     *
     * @param citiesReader a java.io.Reader input stream containing list of cities and countries
     *                     in the specified CSV format
     */

    public Tagger(Reader citiesReader) {
        citiesAndCountries = new HashMap<>();
        lastTaggedCities = new TreeMap<>();
        allTagsCount = 0;
        fillCitiesAndCountries(citiesReader);
    }

    /**
     * Processes an input stream of a text file, tags any cities and outputs result
     * to a text output stream.
     *
     * @param text   a java.io.Reader input stream containing text to be processed
     * @param output a java.io.Writer output stream containing the result of tagging
     */
    public void tagCities(Reader text, Writer output) {
        allTagsCount = 0;
        lastTaggedCities.clear();
        Pattern pat = Pattern.compile(".*\\R|.+\\z");

        try (var textWriter = new BufferedWriter(output); var textReader = new Scanner(new BufferedReader(text))) {

            String textLine;

            while ((textLine = textReader.findWithinHorizon(pat, 0)) != null) {
                textLine = wrapCitiesWithTags(textLine);
                textWriter.write(textLine);
            }
        } catch (IOException e) {
            throw new RuntimeException(TAG_CITIES_IO_PROBLEM_MESSAGE, e);
        }
    }

    /**
     * Returns a collection the top @n most tagged cities' unique names
     * from the last tagCities() invocation. Note that if a particular city has been tagged
     * more than once in the text, just one occurrence of its name should appear in the result.
     * If @n exceeds the total number of cities tagged, return as many as available
     * If tagCities() has not been invoked at all, return an empty collection.
     *
     * @param n the maximum number of top tagged cities to return
     * @return a collection the top @n most tagged cities' unique names
     * from the last tagCities() invocation.
     */
    public Collection<String> getNMostTaggedCities(int n) {
        List<String> sortedCities = new ArrayList<>(lastTaggedCities.keySet());
        sortedCities.sort(getCitiesComparator());

        if (sortedCities.size() <= n) {
            return sortedCities;
        }

        List<String> firstNCities = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            firstNCities.add(sortedCities.get(i));
        }

        return firstNCities;
    }

    /**
     * Returns a collection of all tagged cities' unique names
     * from the last tagCities() invocation. Note that if a particular city has been tagged
     * more than once in the text, just one occurrence of its name should appear in the result.
     * If tagCities() has not been invoked at all, return an empty collection.
     *
     * @return a collection of all tagged cities' unique names
     * from the last tagCities() invocation.
     */
    public Collection<String> getAllTaggedCities() {
        return lastTaggedCities.keySet();
    }

    /**
     * Returns the total number of tagged cities in the input text
     * from the last tagCities() invocation
     * In case a particular city has been taged in several occurences, all must be counted.
     * If tagCities() has not been invoked at all, return 0.
     *
     * @return the total number of tagged cities in the input text
     */
    public long getAllTagsCount() {
        return allTagsCount;
    }

    private void fillCitiesAndCountries(Reader citiesReader) {
        try (var citiesBufferedReader = new BufferedReader(citiesReader)) {
            String line;
            while ((line = citiesBufferedReader.readLine()) != null) {
                String[] cityData = line.split(",");
                String cityName = cityData[0];
                String country = cityData[1];
                citiesAndCountries.put(cityName, country);
            }
        } catch (IOException e) {
            throw new RuntimeException(READ_CITIES_IO_PROBLEM_MESSAGE, e);
        }
    }

    private String wrapCitiesWithTags(String textLine) {
        for (Map.Entry<String, String> entry : citiesAndCountries.entrySet()) {
            textLine = replaceCityWithTags(textLine, entry.getKey(), entry.getValue());
        }

        return textLine;
    }

    private String replaceCityWithTags(String textLine, String cityName, String country) {

        String copy = textLine;
        String replacePattern = String.format(REGEX_REPLACE_CASE_INSENSITIVE_CITY_FORMAT, cityName);
        int replacementsCount = 0;

        while (!copy.replaceFirst(replacePattern, "").equals(copy)) {
            replacementsCount++;
            copy = copy.replaceFirst(replacePattern, "$1$3");
        }

        if (replacementsCount > 0) {
            allTagsCount += replacementsCount;
            lastTaggedCities.putIfAbsent(cityName, 0);
            lastTaggedCities.put(cityName, lastTaggedCities.get(cityName) + replacementsCount);
        }

        return textLine.replaceAll(replacePattern,
                String.format(CITY_TAG_FORMAT, country, cityName));
    }

    private Comparator<String> getCitiesComparator() {
        return new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return Integer.compare(lastTaggedCities.get(s2), lastTaggedCities.get(s1));
            }
        };
    }
}