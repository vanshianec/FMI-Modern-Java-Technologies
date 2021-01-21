package bg.sofia.uni.fmi.mjt.weather.exceptions;

public class WeatherForecastClientException extends RuntimeException {
    public WeatherForecastClientException(String message) {
        super(message);
    }

    public WeatherForecastClientException(String message, Exception e) {
        super(message, e);
    }
}
