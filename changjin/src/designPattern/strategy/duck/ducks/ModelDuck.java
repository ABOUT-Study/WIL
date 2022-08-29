package designPattern.strategy.duck.ducks;

import designPattern.strategy.duck.MuteQuack;

public class ModelDuck extends Duck{
    @Override
    public void display() {
        System.out.println("저는 모형 오리에요");
    }

    public ModelDuck() {
        super.quackBehavior = new MuteQuack();
    }
}
