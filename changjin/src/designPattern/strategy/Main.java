package designPattern.strategy;

import designPattern.strategy.duck.Duck;
import designPattern.strategy.duck.ReadHeadDuck;
import designPattern.strategy.duck.RubberDuck;

public class Main {
    public static void main(String[] args) {
        Duck readHead = new ReadHeadDuck();
        // readHeadDuck은 Duck으로부터 flyBehavior 인스턴스 변수를 상속받는다.
        readHead.perfomeFly();
        readHead.perfomeQuack();

        Duck rubberDuck = new RubberDuck();
        rubberDuck.perfomeFly();
        rubberDuck.perfomeQuack();
    }

}
