package bg.sofia.uni.fmi.mjt.weather;

import bg.sofia.uni.fmi.mjt.weather.dto.WeatherForecast;
import bg.sofia.uni.fmi.mjt.weather.exceptions.LocationNotFoundException;
import bg.sofia.uni.fmi.mjt.weather.exceptions.WeatherForecastClientException;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WeatherForecastClient {

    private static final String API_KEY = "7e411be7a0d4b6a53d35bc2fbb17fae5";
    private static final String SCHEME = "http";
    private static final String HOST = "api.openweathermap.org";
    private static final String PATH = "/data/2.5/weather";
    private static final String QUERY_FORMAT = "q=%s&units=metric&lang=bg&appid=%s";
    private static final int NOT_FOUND = 404;
    private static final int SUCCESS = 200;
    private static final String NON_EXISTING_LOCATION = "Location doesn't exist! Please try with another location.";
    private static final String FAILED_TO_RETRIEVE_INFORMATION = "Could not retrieve the weather information.";
    private static final String URI_INITIALIZATION_ERROR = "A problem occurred creating the URI";
    private static final String REQUEST_SENDING_ERROR = "A problem occurred while trying to send the request";

    private HttpClient weatherHttpClient;

    public WeatherForecastClient(HttpClient weatherHttpClient) {
        this.weatherHttpClient = weatherHttpClient;
    }

    /**
     * Fetches the weather forecast for the specified city.
     *
     * @return the forecast
     * @throws LocationNotFoundException      if the city is not found
     * @throws WeatherForecastClientException if information regarding the weather
     *                                        for this location could not be retrieved
     */

    public WeatherForecast getForecast(String city) throws WeatherForecastClientException {
        URI uri = null;
        try {
            uri = new URI(SCHEME, HOST, PATH, String.format(QUERY_FORMAT, city, API_KEY), null);
        } catch (URISyntaxException e) {
            throw new WeatherForecastClientException(URI_INITIALIZATION_ERROR, e);
        }

        HttpRequest request = HttpRequest.newBuilder().uri(uri).build();

        HttpResponse<String> response = null;
        try {
            response = weatherHttpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new WeatherForecastClientException(REQUEST_SENDING_ERROR, e);
        }

        validateStatusCode(response.statusCode());
        return new Gson().fromJson(response.body(), WeatherForecast.class);
    }

    private void validateStatusCode(int statusCode) throws WeatherForecastClientException {
        if (statusCode == NOT_FOUND) {
            throw new LocationNotFoundException(NON_EXISTING_LOCATION);
        } else if (statusCode != SUCCESS) {
            throw new WeatherForecastClientException(FAILED_TO_RETRIEVE_INFORMATION);
        }
    }
}