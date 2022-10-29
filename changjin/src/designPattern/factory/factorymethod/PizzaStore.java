package designPattern.factory.factorymethod;

public abstract class PizzaStore {

    /*
        추상 팩토리 메소드 사용시 객체를 생성하는 작업을 서브클래스에 캡슐화 시킬 수 있다.
        수퍼클래스의 클라이언트 코드와 서브클래스의 객체 생성 코드를 분리시킬 수 있다.
     */

    public abstract Pizza createPizza(String type);

    public Pizza orderPizza(String type) {
        Pizza pizza;

        pizza = createPizza(type);

        pizza.prepare();
        pizza.bake();
        pizza.cut();
        return pizza;
    }
}
