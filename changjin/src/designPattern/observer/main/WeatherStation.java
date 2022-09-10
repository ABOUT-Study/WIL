package designPattern.observer.main;

import designPattern.observer.observers.CurrentConditionDisplay;
import designPattern.observer.observers.CurrentConditionDisplayKR;
import designPattern.observer.subjects.WeatherData;

public class WeatherStation {
    public static void main(String[] args) {

        WeatherData weatherData = new WeatherData();
        CurrentConditionDisplayKR currentConditionDisplayKR = new CurrentConditionDisplayKR(weatherData);
        CurrentConditionDisplay currentConditionDisplay = new CurrentConditionDisplay(weatherData);

        weatherData.setMeasurements(80, 65, 30.4f);
        weatherData.setMeasurements(82, 70, 29.2f);
    }
}
