package designPattern.factory.abstractfac.main;

import designPattern.factory.abstractfac.NYPizzaStore;
import designPattern.factory.abstractfac.Pizza;
import designPattern.factory.abstractfac.PizzaStore;
import designPattern.factory.factorymethod.ChicagoPizzaStore;

public class PizzaTestDrive {

    public static void main(String[] args) {
        PizzaStore nyStore = new NYPizzaStore();

        nyStore.orderPizza("cheese");

    }

}
