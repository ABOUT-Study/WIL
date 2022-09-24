package designPattern.decorater.condiment;

import designPattern.decorater.beverage.Beverage;

// Mocha는 데코레이터이기 때문에 CondimentDecorator를 확장한다
public class Mocha extends CondimentDecorator { // CondimentDecorator는 Beverage를 확장한다.

    Beverage beverage;

    public Mocha(Beverage beverage) {
        this.beverage = beverage;
    }

    @Override
    public double cost() {
        return .20 + beverage.cost();
    }

    @Override
    public String getDescription() {
        return beverage.getDescription() + ", 모카";
    }
}
