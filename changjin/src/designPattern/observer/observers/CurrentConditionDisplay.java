package designPattern.observer.observers;

import designPattern.observer.DisplayElement;
import designPattern.observer.Observer;
import designPattern.observer.Subject;
import designPattern.observer.subjects.WeatherData;

public class CurrentConditionDisplay implements Observer, DisplayElement {

    private float temperature;
    private float humidity;
    public WeatherData weatherData;

    public CurrentConditionDisplay(WeatherData weatherData) { // 생성자 메소드에서 주제 객체에 옵저버를 추가한다.
        this.weatherData = weatherData;
        weatherData.registerObserver(this);
    }

    @Override
    public void update(float temperature, float humidity, float pressure) {
        this.temperature = temperature;
        this.humidity = humidity;
        display();
    }

    // 풀 방식 (옵저버가 주제 객체의 데이터를 당겨온다)
//    @Override
//    public void update() {
//        this.temperature = weatherData.getTemperature();
//        this.humidity = weatherData.getHumidity();
//        display();
//    }

    @Override
    public void display() {
        System.out.println("Current conditions: " + temperature
                + "F degrees and " + humidity + "% humidity");
    }
}
