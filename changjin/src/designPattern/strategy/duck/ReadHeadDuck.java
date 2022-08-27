package designPattern.strategy.duck;

public class ReadHeadDuck extends Duck {

    public ReadHeadDuck() {
        flyBehavior = new FlyWithWings(); // 상위 클래스의 인스턴스 변수에 FlyWithwing를 대입
        quackBehavior = new Quack();
    }
}
