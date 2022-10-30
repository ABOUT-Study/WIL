package designPattern.factory.abstractfac;

import designPattern.factory.abstractfac.Pizza;
import designPattern.factory.abstractfac.PizzaStore;

public class NYPizzaStore extends PizzaStore {
    public Pizza createPizza(String item) {
        Pizza pizza = null;
        PizzaIngredientFactory ingredientFactory = new NYPizzaIngredientFactory();

        if (item.equals("cheese")) {
            pizza = new CheesePizza(ingredientFactory);
            pizza.setName("New York Style Cheese Pizza");
        }
        return pizza;
    }
}