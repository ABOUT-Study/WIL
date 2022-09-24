package designPattern.decorater.condiment;

import designPattern.decorater.beverage.Beverage;

public class Whip extends CondimentDecorator { // CondimentDecorator는 Beverage를 확장한다.

    Beverage beverage;

    public Whip(Beverage beverage) {
        this.beverage = beverage;
    }

    @Override
    public double cost() {
        return .10 + beverage.cost();
    }

    @Override
    public String getDescription() {
        return beverage.getDescription() + ", 휘핑";
    }
}
