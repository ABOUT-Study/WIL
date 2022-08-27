package designPattern.strategy.duck;

public abstract class Duck {

    FlyBehavior flyBehavior; // 행동만을 나타내느 클래스
    QuackBehavior quackBehavior; // 행동만을 나타내느 클래스

    public void swim() {
        System.out.println("수영하기");
    }

    public abstract void display();

    public void perfomeQuack() {
        quackBehavior.quack();
    }

    public void perfomeFly() {
        flyBehavior.fly();
    }
}
