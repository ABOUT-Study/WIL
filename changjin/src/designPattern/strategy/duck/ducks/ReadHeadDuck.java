package designPattern.strategy.duck.ducks;

import designPattern.strategy.duck.FlyWithWings;
import designPattern.strategy.duck.Quack;

public class ReadHeadDuck extends Duck {

    public ReadHeadDuck() {
        flyBehavior = new FlyWithWings(); // 상위 클래스의 인스턴스 변수에 FlyWithwing를 대입
        quackBehavior = new Quack();
    }

    @Override
    public void display() {
        System.out.println("저는 빨간머리 오리에요");
    }
}
