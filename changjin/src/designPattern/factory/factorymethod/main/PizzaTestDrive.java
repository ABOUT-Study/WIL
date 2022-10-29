package designPattern.factory.factorymethod.main;

import designPattern.factory.factorymethod.ChicagoPizzaStore;
import designPattern.factory.factorymethod.NYPizzaStore;
import designPattern.factory.factorymethod.Pizza;
import designPattern.factory.factorymethod.PizzaStore;

public class PizzaTestDrive {

    public static void main(String[] args) {
        PizzaStore nyStore = new NYPizzaStore();
        PizzaStore chicagoStore = new ChicagoPizzaStore();

        // 지점과 주어진 타입에 따라 다른 피자를 만든다

        Pizza nyCheesePizza = nyStore.orderPizza("cheese");
//        Pizza nyClamPizza = nyStore.orderPizza("clam");

        Pizza chicagoCheesePizza = chicagoStore.orderPizza("cheese");
//        Pizza chicagoClamPizza = chicagoStore.orderPizza("clam");
    }
}
