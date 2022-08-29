package designPattern.strategy.duck.ducks;

import designPattern.strategy.duck.FlyBehavior;
import designPattern.strategy.duck.FlyNoWay;
import designPattern.strategy.duck.QuackBehavior;

public abstract class Duck {

    FlyBehavior flyBehavior; // 행동만을 나타내느 클래스

    QuackBehavior quackBehavior; // 행동만을 나타내느 클래스

    public void swim() {
        System.out.println("수영하기");
    }

    public void setFlyBehavior(FlyBehavior flyBehavior) {
        this.flyBehavior = flyBehavior;
    }

    public void setQuackBehavior(QuackBehavior quackBehavior) {
        this.quackBehavior = quackBehavior;
    }

    public abstract void display();

    public void perfomeQuack() {
        quackBehavior.quack();
    }

    public void perfomeFly() {
        flyBehavior.fly();
    }
}
