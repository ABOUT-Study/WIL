package designPattern.factory.abstractfac;

public interface PizzaIngredientFactory {

    Dough createDough();
    Sauce createSauce();

}
