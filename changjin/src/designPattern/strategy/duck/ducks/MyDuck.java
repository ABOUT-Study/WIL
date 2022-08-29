package designPattern.strategy.duck.ducks;

import designPattern.strategy.duck.FlyWithWings;
import designPattern.strategy.duck.Quack;

public class MyDuck extends Duck {

    public MyDuck() {
    }

    @Override
    public void display() {
        System.out.println("저는 빨간머리 오리에요");
    }
}
