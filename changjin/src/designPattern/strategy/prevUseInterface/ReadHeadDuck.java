package designPattern.strategy.prevUseInterface;

public class ReadHeadDuck extends Duck implements Flyable, Quackable {



    @Override
    public void fly() {
        System.out.println("날고있어요");
    }

    @Override
    public void quack() {
        System.out.println("꽥꽥");
    }

    @Override
    public void display() {
        System.out.println("저는 빨간 머리 오리에요");
    }
}
