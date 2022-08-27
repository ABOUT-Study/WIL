package designPattern.strategy.prevUseInterface.runner;

import designPattern.strategy.prevUseInterface.Duck;
import designPattern.strategy.prevUseInterface.Flyable;
import designPattern.strategy.prevUseInterface.Quackable;

public class MallardDuck extends Duck implements Flyable, Quackable {
    @Override
    public void display() {
        System.out.println("저는 물오리에요");
    }

    @Override
    public void fly() {
        System.out.println("날고있어요");
    }

    @Override
    public void quack() {
        System.out.println("꽥꽥");
    }
}
