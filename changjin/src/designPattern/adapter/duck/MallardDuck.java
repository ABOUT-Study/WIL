package designPattern.adapter.duck;


import designPattern.adapter.duck.Duck;

// Adaptee
public class MallardDuck implements Duck {


    @Override
    public void quack() {
        System.out.println("quack");
    }

    @Override
    public void fly() {
        System.out.println("fly");
    }
}
