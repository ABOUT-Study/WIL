package designPattern.decorater.main;

import designPattern.decorater.beverage.Beverage;
import designPattern.decorater.beverage.Espresso;
import designPattern.decorater.beverage.HouseBlend;
import designPattern.decorater.condiment.Mocha;
import designPattern.decorater.condiment.Soy;
import designPattern.decorater.condiment.Whip;

public class Main {
    public static void main(String[] args) {
        Beverage beverage = new Espresso();
        System.out.println(beverage.getDescription() + ", " + beverage.cost());

        Beverage beverage2 = new HouseBlend();
        System.out.println(beverage2.getDescription() + ", " + beverage2.cost());
        beverage2 = new Mocha(beverage2);
        beverage2 = new Mocha(beverage2);
        beverage2 = new Soy(beverage2);
        System.out.println(beverage2.getDescription() + ", " + beverage2.cost());

        Beverage beverage3 = new HouseBlend();
        beverage3 = new Soy(beverage3);
        beverage3 = new Mocha(beverage3);
        beverage3 = new Whip(beverage3);
        System.out.println(beverage3.getDescription() + ", " + beverage3.cost());


    }
}
