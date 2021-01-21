package bg.sofia.uni.fmi.mjt.weather.dto;

import java.util.Arrays;
import java.util.Objects;

public class WeatherForecast {

    private WeatherCondition[] weather;
    private WeatherData main;

    public WeatherForecast(WeatherCondition[] weather, WeatherData main) {
        this.weather = weather;
        this.main = main;
    }

    public WeatherCondition[] getWeather() {
        return weather;
    }

    public void setWeather(WeatherCondition[] weather) {
        this.weather = weather;
    }

    public WeatherData getMain() {
        return main;
    }

    public void setMain(WeatherData main) {
        this.main = main;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        WeatherForecast that = (WeatherForecast) o;
        return Arrays.equals(weather, that.weather) && Objects.equals(main, that.main);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(main);
        result = 31 * result + Arrays.hashCode(weather);
        return result;
    }
}
