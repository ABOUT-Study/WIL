package designPattern.strategy.prev;

import designPattern.strategy.duck.FlyBehavior;
import designPattern.strategy.duck.QuackBehavior;

public abstract class Duck {

    public abstract void display();

    public void quack() {
        System.out.println("꽥꽥");
    }

    public void swim() {
        System.out.println("수영하기");
    }

    // 새로 추가된 기능
    public void fly() {
        System.out.println("날고 있어요");
    }
}
