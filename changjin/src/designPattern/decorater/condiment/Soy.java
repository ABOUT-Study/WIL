package designPattern.decorater.condiment;

import designPattern.decorater.beverage.Beverage;

public class Soy extends CondimentDecorator { // CondimentDecorator는 Beverage를 확장한다.

    Beverage beverage;

    public Soy(Beverage beverage) {
        this.beverage = beverage;
    }

    @Override
    public double cost() {
        return .15 + beverage.cost();
    }

    @Override
    public String getDescription() {
        return beverage.getDescription() + ", 두유";
    }
}
