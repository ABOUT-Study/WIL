package designPattern.factory.abstractFactory.main;

import designPattern.factory.abstractFactory.NYPizzaStore;
import designPattern.factory.abstractFactory.Pizza;
import designPattern.factory.abstractFactory.PizzaStore;

public class PizzaTestDrive {

    public static void main(String[] args) {
        PizzaStore nyStore = new NYPizzaStore();
//        PizzaStore chicagoStore = new ChicagoPizzaStore();

        Pizza pizza = nyStore.orderPizza("cheese");
        System.out.println("Ethan ordered a " + pizza.getName() + "\n");

    }
}
