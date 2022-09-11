package designPattern.observer.subjects;

import designPattern.observer.Observer;
import designPattern.observer.Subject;

import java.util.ArrayList;
import java.util.List;

public class WeatherData implements Subject {

    public List<Observer> observers;
    private float temperature;
    private float humidity;
    private float pressure;

    public WeatherData() {
        observers = new ArrayList<>();
    }

    @Override
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        int i = observers.indexOf(observer);
        if (i >= 0) {
            observers.remove(i);
        }
    }

    @Override
    public void notifyObserver() { // 이벤트 발생시 옵저버들에게 변경을 알린다. 푸시 기반으로 동작한다.
        for (Observer observer : observers) {
            observer.update(this.temperature, this.humidity, this.pressure);
        }
    }

    // 풀 방식
//    @Override
//    public void notifyObserver() {
//        for (Observer observer : observers) {
//            observer.update();
//        }
//    }

    public void measurementsChanged() {
        notifyObserver();
    }

    public void setMeasurements(float temperature, float humidity, float pressure) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
        measurementsChanged();
    }

    public float getTemperature() {
        return temperature;
    }

    public float getHumidity() {
        return humidity;
    }

    public float getPressure() {
        return pressure;
    }
}
