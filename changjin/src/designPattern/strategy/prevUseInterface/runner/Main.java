package designPattern.strategy.prevUseInterface.runner;

import designPattern.strategy.prevUseInterface.Duck;
import designPattern.strategy.prevUseInterface.RubberDuck;

public class Main {
    public static void main(String[] args) {

        Duck rubberDuck = new RubberDuck();
        rubberDuck.swim();

        MallardDuck mallardDuck = new MallardDuck();
        // 다형성을 사용할 수 없다.
        mallardDuck.fly();

    }
}
