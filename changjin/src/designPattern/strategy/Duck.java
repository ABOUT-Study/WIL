package designPattern.strategy;

import designPattern.strategy.duck.FlyBehavior;

public abstract class Duck {
    FlyBehavior flyBehavior;

    public void swim() {
        System.out.println("오리가 수영중 ~~");
    }

    public abstract void display();

    public void performFly() {
        this.flyBehavior.fly();
    }
}