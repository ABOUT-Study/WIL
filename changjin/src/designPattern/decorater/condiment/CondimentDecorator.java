package designPattern.decorater.condiment;

import designPattern.decorater.beverage.Beverage;

public abstract class CondimentDecorator extends Beverage {

    // 모든 첨가물 데코레이터에서 getDescription을 메소드를 구현하도록 만들 계획이므로 추상 메소드로 선언한다.
    public abstract String getDescription();
}
