package bg.sofia.uni.fmi.mjt.weather;

import bg.sofia.uni.fmi.mjt.weather.dto.WeatherForecast;
import bg.sofia.uni.fmi.mjt.weather.exceptions.LocationNotFoundException;
import bg.sofia.uni.fmi.mjt.weather.exceptions.WeatherForecastClientException;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WeatherForecastClientTest {

    private WeatherForecastClient weatherClient;

    @Mock
    private HttpClient httpClientMock;

    @Mock
    private HttpResponse<String> httpResponseMock;

    @Before
    public void init() {
        weatherClient = new WeatherForecastClient(httpClientMock);
    }

    @Test
    public void testGetForecastWithExistingCity() throws InterruptedException, IOException {
        when(httpClientMock.send(Mockito.any(HttpRequest.class),
                ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(httpResponseMock);
        String cityInJson = "{\"coord\":{\"lon\":24.6167,\"lat\":43.4167},\"weather\":"
                + "[{\"id\":600,\"main\":\"Snow\",\"description\":\"слаб снеговалеж\",\"icon\""
                + ":\"13n\"}],\"base\":\"stations\",\"main\":{\"temp\":-1,\"feels_like\""
                + ":-8.43,\"temp_min\":-1,\"temp_max\":-1,\"pressure\":1015,\"humidity\":86},"
                + "\"visibility\":10000,\"wind\":{\"speed\":7.2,\"deg\":270},\"snow\":{\"1h\":0.44},"
                + "\"clouds\":{\"all\":90},\"dt\":1610816503,\"sys\":{\"type\":1,\"id\":6369,\"country\":"
                + "\"BG\",\"sunrise\":1610776238,\"sunset\":1610809893},\"timezone\":7200,\"id\":728203,"
                + "\"name\":\"Плевен\",\"cod\":200}";
        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn(cityInJson);
        Gson gson = new Gson();
        WeatherForecast expected = gson.fromJson(cityInJson, WeatherForecast.class);
        WeatherForecast actual = weatherClient.getForecast("Pleven");

        assertEquals(expected.getMain().getFeelsLike(), actual.getMain().getFeelsLike(), 0.01);
        assertEquals(expected.getMain().getTemp(), actual.getMain().getTemp(), 0.01);
        assertEquals(expected.getWeather().length, actual.getWeather().length);
        for (int i = 0; i < expected.getWeather().length; i++) {
            assertEquals(expected.getWeather()[i].getDescription(), actual.getWeather()[i].getDescription());
        }

        /* Test if equals works ok as well */
        assertEquals(expected.getMain(), actual.getMain());
        assertArrayEquals(expected.getWeather(), actual.getWeather());
        assertEquals(expected, actual);
        assertNotEquals(new WeatherForecast(null, null), actual);
    }

    @Test(expected = LocationNotFoundException.class)
    public void testGetForecastWithInvalidCity() throws InterruptedException, IOException {
        when(httpClientMock.send(Mockito.any(HttpRequest.class),
                ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(httpResponseMock);
        when(httpResponseMock.statusCode()).thenReturn(404);
        weatherClient.getForecast("invalid missing city###");
    }

    @Test(expected = WeatherForecastClientException.class)
    public void testGetForecastWithServerError() throws InterruptedException, IOException {
        when(httpClientMock.send(Mockito.any(HttpRequest.class),
                ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(httpResponseMock);
        when(httpResponseMock.statusCode()).thenReturn(500);
        weatherClient.getForecast("something bad happened to the server");
    }

    @Test(expected = WeatherForecastClientException.class)
    public void testGetForecastWithRequestError() throws InterruptedException, IOException {
        when(httpClientMock.send(Mockito.any(HttpRequest.class),
                ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
                .thenThrow(IOException.class);
        weatherClient.getForecast("an error is about to launch");
    }
}