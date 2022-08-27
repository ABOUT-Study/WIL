package designPattern.strategy.duck.runner;

import designPattern.strategy.duck.*;
import designPattern.strategy.duck.ducks.Duck;
import designPattern.strategy.duck.ducks.ModelDuck;
import designPattern.strategy.duck.ducks.ReadHeadDuck;
import designPattern.strategy.duck.ducks.RubberDuck;

public class Main {
    public static void main(String[] args) {
        Duck readHead = new ReadHeadDuck();
        // readHeadDuck은 Duck으로부터 flyBehavior 인스턴스 변수를 상속받는다.
        readHead.perfomeFly();
        readHead.perfomeQuack();

        Duck rubberDuck = new RubberDuck();
        rubberDuck.perfomeFly();
        rubberDuck.perfomeQuack();

        Duck modelDuck = new ModelDuck();
        modelDuck.setFlyBehavior(new FlyRocketPowered());
        modelDuck.perfomeFly();

        // 실행 중에 오리의 행동을 바꾸고 싶다면 원하는 행동에 해당하는 Setter 만 변경하면 된다.
        rubberDuck.setFlyBehavior(new FlyRocketPowered());
        rubberDuck.perfomeFly();
    }

}
