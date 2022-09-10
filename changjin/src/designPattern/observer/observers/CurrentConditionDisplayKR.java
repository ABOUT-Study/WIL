package designPattern.observer.observers;

import designPattern.observer.DisplayElement;
import designPattern.observer.Observer;
import designPattern.observer.Subject;

public class CurrentConditionDisplayKR implements Observer, DisplayElement {

    private float temperature;
    private float humidity;
    public Subject weatherData;

    public CurrentConditionDisplayKR(Subject weatherData) {
        this.weatherData = weatherData;
        weatherData.registerObserver(this);
    }

    @Override
    public void update(float temperature, float humidity, float pressure) {
        this.temperature = temperature;
        this.humidity = humidity;
        display();
    }

    @Override
    public void display() {
        System.out.println("현재 날씨: " + (temperature - 32) * 5/9
        + "C, 습도 : " + humidity + "%");
    }
}
