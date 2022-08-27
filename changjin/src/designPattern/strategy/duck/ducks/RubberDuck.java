package designPattern.strategy.duck.ducks;

import designPattern.strategy.duck.FlyNoWay;
import designPattern.strategy.duck.MuteQuack;

public class RubberDuck extends Duck {

    public RubberDuck() {
        flyBehavior = new FlyNoWay();
        quackBehavior = new MuteQuack();
    }

    @Override
    public void display() {
        System.out.println("저는 모형 오리에요");
    }
}
