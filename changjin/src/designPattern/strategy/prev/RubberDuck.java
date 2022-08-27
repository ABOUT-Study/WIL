package designPattern.strategy.prev;

public class RubberDuck extends Duck{

    @Override
    public void display() {
        System.out.println("저는 고무오리에요");
    }

    @Override
    public void fly() {
        System.out.println("저는 날지 못해요");
    }
}
