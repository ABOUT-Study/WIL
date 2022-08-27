package designPattern.strategy.duck;

public class FlyWithWings implements FlyBehavior {

    @Override
    public void fly() {
        System.out.println("저는 날고 있어요.");
    }
}
