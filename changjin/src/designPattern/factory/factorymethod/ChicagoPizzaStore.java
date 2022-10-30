package designPattern.factory.factorymethod;

public class ChicagoPizzaStore extends PizzaStore{
    @Override
    public Pizza createPizza(String item) {
        if (item.equals("cheese")) {
            return new ChicagoStyleCheesePizza();
//        } else if (item.equals("veggie")) {
//            return new NYStyleVeggiePizza();
//        } else if (item.equals("clam")) {
//            return new NYStyleClamPizza();
        } else {
            return null;
        }
    }
}
